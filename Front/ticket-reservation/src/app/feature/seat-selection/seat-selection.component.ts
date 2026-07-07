import { DatePipe, DecimalPipe, Location } from '@angular/common';
import { Component, computed, inject, resource, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PaymentService } from '../../core/services/payment.service';
import { ReservationService } from '../../core/services/reservation.service';
import { ShowService } from '../../core/services/show.service';
import { HallDetails, MovieDetails, ShowDetails, TicketInfo } from '../../core/models/show.model';

interface SeatSelectionData {
  show: ShowDetails;
  movie: MovieDetails;
  hall: HallDetails;
  tickets: TicketInfo[];
}

interface SeatCell {
  label: string;
  ticket: TicketInfo | null;
}

interface SeatRow {
  letter: string;
  seats: SeatCell[];
}

// Flat fee added once per reservation, regardless of seat count.
const SERVICE_FEE = 1.5;

@Component({
  selector: 'app-seat-selection',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './seat-selection.component.html'
})
export class SeatSelectionComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private showService = inject(ShowService);
  private reservationService = inject(ReservationService);
  private paymentService = inject(PaymentService);
  private authService = inject(AuthService);

  showId = this.route.snapshot.paramMap.get('showId') ?? '';

  selectedIds = signal<Set<string>>(new Set());
  submitting = signal(false);
  submitError = signal<string | null>(null);

  dataResource = resource({
    params: () => this.showId,
    loader: async ({ params }) => {
      const show = await this.showService.getShow(params);
      const [movie, hall, tickets] = await Promise.all([
        this.showService.getMovie(show.movieId),
        this.showService.getHall(show.hallId),
        this.showService.getTicketsByShow(params)
      ]);
      return { show, movie, hall, tickets } satisfies SeatSelectionData;
    }
  });

  rows = computed<SeatRow[]>(() => {
    const data = this.dataResource.value();
    if (!data) return [];

    const ticketsBySeat = new Map(data.tickets.map((t) => [t.seatNumber, t]));
    const rowLetters = Array.from({ length: data.hall.rowCount }, (_, i) =>
      String.fromCharCode(65 + i)
    );

    return rowLetters.map((letter) => ({
      letter,
      seats: Array.from({ length: data.hall.seatsPerRow }, (_, i) => {
        const label = `${letter}${i + 1}`;
        return { label, ticket: ticketsBySeat.get(label) ?? null };
      })
    }));
  });

  selectedTickets = computed<TicketInfo[]>(() => {
    const data = this.dataResource.value();
    if (!data) return [];
    const ids = this.selectedIds();
    return data.tickets
      .filter((t) => ids.has(t.id))
      .sort((a, b) => a.seatNumber.localeCompare(b.seatNumber));
  });

  seatSubtotal = computed(() => this.selectedTickets().reduce((sum, t) => sum + t.price, 0));

  serviceFee = computed(() => (this.selectedTickets().length > 0 ? SERVICE_FEE : 0));

  total = computed(() => this.seatSubtotal() + this.serviceFee());

  pricePerSeat = computed(() => {
    const tickets = this.selectedTickets();
    if (tickets.length === 0) return this.dataResource.value()?.show.price ?? 0;
    return this.seatSubtotal() / tickets.length;
  });

  toggleSeat(seat: SeatCell): void {
    if (!seat.ticket || seat.ticket.status !== 'AVAILABLE') return;

    const ids = new Set(this.selectedIds());
    if (ids.has(seat.ticket.id)) {
      ids.delete(seat.ticket.id);
    } else {
      ids.add(seat.ticket.id);
    }
    this.selectedIds.set(ids);
  }

  isSelected(seat: SeatCell): boolean {
    return !!seat.ticket && this.selectedIds().has(seat.ticket.id);
  }

  seatClasses(seat: SeatCell): string {
    const base =
      'w-8 h-8 rounded-md text-[11px] font-semibold flex items-center justify-center transition-colors select-none';

    if (!seat.ticket) {
      return `${base} bg-neutral-900 text-neutral-700 cursor-default`;
    }
    if (this.isSelected(seat)) {
      return `${base} bg-amber-500 text-black cursor-pointer`;
    }
    switch (seat.ticket.status) {
      case 'BOOKED':
        return `${base} bg-red-800/70 text-red-200 cursor-not-allowed`;
      case 'RESERVED':
        return `${base} bg-yellow-800/70 text-yellow-200 cursor-not-allowed`;
      default:
        return `${base} bg-neutral-700 text-neutral-200 cursor-pointer hover:bg-neutral-600`;
    }
  }

  goBack(): void {
    this.location.back();
  }

  async confirmReservation(): Promise<void> {
    const userId = this.authService.getUserId();
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    const tickets = this.selectedTickets();
    if (tickets.length === 0 || this.submitting()) return;

    this.submitting.set(true);
    this.submitError.set(null);

    try {
      const reservation = await this.reservationService.createReservation({
        userId,
        showId: this.showId,
        ticketIds: tickets.map((t) => t.id)
      });

      const readyReservation = await this.reservationService.waitForSagaReady(
        reservation.id,
        this.total()
      );

      const checkout = await this.paymentService.createCheckoutSession({
        reservationId: readyReservation.id,
        userId,
        showId: this.showId,
        ticketIds: tickets.map((t) => t.id),
        totalPrice: readyReservation.totalPrice
      });

      window.location.href = checkout.checkoutUrl;
    } catch {
      this.submitError.set('Could not complete the reservation. Please try again.');
    } finally {
      this.submitting.set(false);
    }
  }
}
