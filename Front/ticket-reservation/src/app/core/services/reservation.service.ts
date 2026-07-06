import { HttpClient } from '@angular/common/http';
import { inject, Injectable, resource, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { ReservationSummary } from '../models/reservation.model';
import { MOCK_RESERVATIONS } from './mock-reservations';

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
        await new Promise((resolve) => setTimeout(resolve, 400)); // simulate network latency
        return MOCK_RESERVATIONS;
      }

      return firstValueFrom(
        this.http.get<ReservationSummary[]>(`${this.baseUrl}/user/${params}`)
      );
    }
  });
}