import { Component, input } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ReservationSummary } from '../../../core/models/reservation.model';

@Component({
  selector: 'app-reservation-card',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './reservation-card.component.html'
})
export class ReservationCardComponent {
  reservation = input.required<ReservationSummary>();

  statusClasses(): string {
    const base = 'px-2.5 py-0.5 rounded-full text-xs font-medium';
    switch (this.reservation().status) {
      case 'CONFIRMED':
        return `${base} bg-emerald-500/15 text-emerald-400`;
      case 'PENDING':
        return `${base} bg-amber-500/15 text-amber-400`;
      case 'CANCELLED':
        return `${base} bg-red-500/15 text-red-400`;
    }
  }
}