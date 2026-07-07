import { DecimalPipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BackendReservationResponse, ReservationService } from '../../../core/services/reservation.service';
import { PaymentService } from '../../../core/services/payment.service';

const POLL_DELAY_MS = 1000;
const MAX_ATTEMPTS = 30;

@Component({
  selector: 'app-checkout-processing',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './checkout-processing.component.html'
})
export class CheckoutProcessingComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private reservationService = inject(ReservationService);
  private paymentService = inject(PaymentService);
  private timeoutId: ReturnType<typeof setTimeout> | null = null;
  private attempts = 0;
  private paymentStarted = false;

  reservationId = this.route.snapshot.paramMap.get('reservationId') ?? '';
  reservation = signal<BackendReservationResponse | null>(null);
  message = signal('Creating your reservation...');
  error = signal<string | null>(null);

  ngOnInit(): void {
    if (!this.reservationId) {
      this.error.set('Reservation id is missing.');
      return;
    }

    void this.pollReservation();
  }

  ngOnDestroy(): void {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
  }

  private async pollReservation(): Promise<void> {
    this.attempts += 1;

    try {
      const reservation = await this.reservationService.getReservationById(this.reservationId);
      this.reservation.set(reservation);

      if (reservation.status === 'CANCELLED') {
        this.error.set('Those seats could not be reserved. Please choose different seats.');
        return;
      }

      if (reservation.status === 'CONFIRMED') {
        this.error.set('This reservation is already confirmed.');
        return;
      }

      if (reservation.status === 'PENDING' && reservation.totalPrice > 0) {
        await this.completeTemporarySuccessfulPayment(reservation);
        return;
      }

      this.message.set('Reserving your seats...');
    } catch {
      this.message.set('Still waiting for the booking services...');
    }

    if (this.attempts >= MAX_ATTEMPTS) {
      this.error.set('Checkout is taking longer than expected. Please check your reservations.');
      return;
    }

    this.timeoutId = setTimeout(() => void this.pollReservation(), POLL_DELAY_MS);
  }

  private async completeTemporarySuccessfulPayment(reservation: BackendReservationResponse): Promise<void> {
    if (this.paymentStarted) return;
    this.paymentStarted = true;
    this.message.set('Seats reserved. Simulating successful Stripe payment...');

    /*
     * Stripe checkout path kept for later webhook testing:
     *
     * const checkout = await this.paymentService.createCheckoutSession({
     *   reservationId: reservation.id,
     *   userId: reservation.userId,
     *   showId: reservation.showId,
     *   ticketIds: reservation.ticketIds ?? [],
     *   totalPrice: reservation.totalPrice
     * });
     *
     * window.location.assign(checkout.checkoutUrl);
     */

    await this.paymentService.simulateSuccessfulPayment({
      reservationId: reservation.id,
      userId: reservation.userId,
      showId: reservation.showId,
      ticketIds: reservation.ticketIds ?? [],
      totalPrice: reservation.totalPrice
    });

    this.message.set('Stripe payment completed successfully. Confirming your booking...');
    await this.waitForReservationConfirmation();
    await this.router.navigate(['/payment', 'success']);
  }

  private async waitForReservationConfirmation(): Promise<void> {
    for (let attempt = 0; attempt < 10; attempt += 1) {
      const reservation = await this.reservationService.getReservationById(this.reservationId);
      this.reservation.set(reservation);

      if (reservation.status === 'CONFIRMED') {
        return;
      }

      await new Promise((resolve) => setTimeout(resolve, POLL_DELAY_MS));
    }
  }
}
