import { HttpClient } from '@angular/common/http';
import { inject, Injectable, resource, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { ReservationSummary } from '../models/reservation.model';

export interface BackendReservationResponse {
  id: string;
  userId: string;
  showId: string;
  totalPrice: number;
  status: 'CONFIRMED' | 'PENDING' | 'CANCELLED';
  createdAt: string;
  ticketIds: string[];
}

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/reservations`;

  userId = signal<string | null>(null);

  reservationsResource = resource({
    params: () => this.userId(),
    loader: async ({ params }) => {
      if (!params) {
        return [] as ReservationSummary[];
      }

      if (environment.useMockData) {
        await new Promise((resolve) => setTimeout(resolve, 400)); // simulate latency
        const { MOCK_RESERVATIONS } = await import('./mock-reservations');
        return MOCK_RESERVATIONS;
      }

      // 2. Query the backend endpoint: GET /reservations/user/{userId}
      const rawReservations = await firstValueFrom(
        this.http.get<BackendReservationResponse[]>(`${this.baseUrl}/user/${params}`)
      );

      // 3. Map backend DTO array to frontend ReservationSummary array
      return rawReservations.map(res => this.mapToReservationSummary(res));
    }
  });

  /**
   * Translates backend data format to frontend UI model format.
   */
  private mapToReservationSummary(res: BackendReservationResponse): ReservationSummary {
    return {
      id: res.id,
      status: res.status,
      totalPrice: res.totalPrice,
      createdAt: res.createdAt,
      // Map the length of ticketIds to seatCount
      seatCount: res.ticketIds ? res.ticketIds.length : 0,
      // Temporary placeholder show info until frontend-to-backend integration is fully finished
      show: {
        id: res.showId,
        movieTitle: `Movie (Show ID: ${res.showId.substring(0, 8)}...)`,
        posterUrl: 'https://picsum.photos/seed/placeholder/300/400',
        startTime: res.createdAt, // Fallback to reservation creation date
        hallName: 'Screen TBD'
      }
    };
  }
}