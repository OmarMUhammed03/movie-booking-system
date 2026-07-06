// src/app/core/services/reservation.service.ts
import { HttpClient } from '@angular/common/http';
import { inject, Injectable, resource, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { ReservationSummary } from '../models/reservation.model';

// 1. Define backend interfaces
export interface BackendReservationResponse {
  id: string;
  userId: string;
  showId: string;
  totalPrice: number;
  status: 'CONFIRMED' | 'PENDING' | 'CANCELLED';
  createdAt: string;
  ticketIds: string[];
}

interface ShowResponse {
  id: string;
  movieId: string;
  hallId: string;
  startTime: string;
  endTime: string;
  price: number;
}

interface MovieResponse {
  id: string;
  title: string;
  posterUrl: string;
}

interface HallResponse {
  id: string;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private http = inject(HttpClient);
  userId = signal<string | null>(null);

  reservationsResource = resource({
    params: () => this.userId(),
    loader: async ({ params }) => {
      if (!params) {
        return [] as ReservationSummary[];
      }

      if (environment.useMockData) {
        await new Promise((resolve) => setTimeout(resolve, 400));
        const { MOCK_RESERVATIONS } = await import('./mock-reservations');
        return MOCK_RESERVATIONS;
      }

      // A. Fetch raw reservations from reservation-service
      const rawReservations = await firstValueFrom(
        this.http.get<BackendReservationResponse[]>(`${environment.reservationUrl}/user/${params}`)
      );

      // B. Fetch movie, show, and hall details in parallel for each reservation
      const summaryPromises = rawReservations.map(async (res) => {
        try {
          // 1. Call show-service: GET /api/shows/{id}
          const show = await firstValueFrom(
            this.http.get<ShowResponse>(`${environment.showUrl}/shows/${res.showId}`)
          );

          // 2. Call movie-service: GET /api/movies/{id}
          const movie = await firstValueFrom(
            this.http.get<MovieResponse>(`${environment.movieUrl}/movies/${show.movieId}`)
          );

          // 3. Call show-service: GET /api/halls/{id}
          const hall = await firstValueFrom(
            this.http.get<HallResponse>(`${environment.showUrl}/halls/${show.hallId}`)
          );

          // Map to complete ReservationSummary
          return {
            id: res.id,
            status: res.status,
            totalPrice: res.totalPrice,
            createdAt: res.createdAt,
            seatCount: res.ticketIds ? res.ticketIds.length : 0,
            show: {
              id: res.showId,
              movieTitle: movie.title,
              posterUrl: movie.posterUrl,
              startTime: show.startTime,
              hallName: hall.name
            }
          } as ReservationSummary;

        } catch (error) {
          console.warn(`Could not load full details for showId: ${res.showId}`, error);
          // Fallback so the page doesn't crash if one service fails
          return {
            id: res.id,
            status: res.status,
            totalPrice: res.totalPrice,
            createdAt: res.createdAt,
            seatCount: res.ticketIds ? res.ticketIds.length : 0,
            show: {
              id: res.showId,
              movieTitle: `Show (${res.showId.substring(0, 8)})`,
              posterUrl: 'https://picsum.photos/seed/placeholder/300/400',
              startTime: res.createdAt,
              hallName: 'Screen TBD'
            }
          } as ReservationSummary;
        }
      });

      return Promise.all(summaryPromises);
    }
  });
}