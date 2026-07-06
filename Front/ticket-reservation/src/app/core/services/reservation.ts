import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation } from '../../models/reservation-model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = 'http://localhost:8080/api/v1/reservations'; // Adjust to your Gateway/Service URL

  // Signal holding the state of reservations
  #reservations = signal<Reservation[]>([]);
  
  // Public read-only signal for components to consume
  readonly reservations = this.#reservations.asReadonly();

  constructor(private http: HttpClient) {}

  // Fetch from your reservation-service backend
  loadMyReservations(): void {
    // For now, we can fill it with mock data matching your image to test the UI first
    const mockData: Reservation[] = [
      {
        id: 'r1',
        movieTitle: 'Neon Horizon',
        moviePosterUrl: 'assets/images/neon-horizon.jpg', // Replace with placeholder or real URLs
        status: 'CONFIRMED',
        showDateTime: '2026-07-04T18:30:00',
        location: 'Aumra IMAX',
        totalPrice: 29.00
      },
      {
        id: 'r2',
        movieTitle: 'The Last Ember',
        moviePosterUrl: 'assets/images/the-last-ember.jpg',
        status: 'PENDING',
        showDateTime: '2026-07-05T19:00:00',
        location: 'Aurora IMAX',
        totalPrice: 43.50
      },
      {
        id: 'r3',
        movieTitle: 'Midnight Circuit',
        moviePosterUrl: 'assets/images/midnight-circuit.jpg',
        status: 'CANCELLED',
        showDateTime: '2026-07-04T20:15:00',
        location: 'Screen 3 — Standard',
        totalPrice: 14.50
      }
    ];

    this.#reservations.set(mockData);

    /* Uncomment this later when we integrate the real endpoint:
    this.http.get<Reservation[]>(this.apiUrl).subscribe({
      next: (data) => this.#reservations.set(data),
      error: (err) => console.error('Failed to load reservations', err)
    });
    */
  }
}