import { Component, inject, OnInit } from '@angular/core';
import { ReservationService } from '../../../core/services/reservation.service';
import { AuthService } from '../../../core/services/auth.service';
import { ReservationCardComponent } from '../reservation-card/reservation-card.component';

@Component({
  selector: 'app-reservations-list',
  imports: [ReservationCardComponent],
  templateUrl: './reservations-list.component.html'
})
export class ReservationsListComponent implements OnInit {
  private reservationService = inject(ReservationService);
  private authService = inject(AuthService);

  resource = this.reservationService.reservationsResource;

  ngOnInit(): void {
    this.reservationService.userId.set(this.authService.getUserId());
  }
}