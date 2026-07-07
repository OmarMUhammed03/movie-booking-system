// src/app/core/services/show.service.ts
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { HallDetails, MovieDetails, ShowDetails, TicketInfo } from '../models/show.model';

@Injectable({ providedIn: 'root' })
export class ShowService {
  private http = inject(HttpClient);

  getShow(showId: string): Promise<ShowDetails> {
    return firstValueFrom(
      this.http.get<ShowDetails>(`${environment.showUrl}/shows/${showId}`)
    );
  }

  getMovie(movieId: string): Promise<MovieDetails> {
    return firstValueFrom(
      this.http.get<MovieDetails>(`${environment.movieUrl}/movies/${movieId}`)
    );
  }

  getHall(hallId: string): Promise<HallDetails> {
    return firstValueFrom(
      this.http.get<HallDetails>(`${environment.showUrl}/halls/${hallId}`)
    );
  }

  getShows(): Promise<ShowDetails[]> {
    return firstValueFrom(
      this.http.get<ShowDetails[]>(`${environment.showUrl}/shows`)
    );
  }

  getTicketsByShow(showId: string): Promise<TicketInfo[]> {
    return firstValueFrom(
      this.http.get<TicketInfo[]>(`${environment.showUrl}/tickets`, {
        params: { showId }
      })
    );
  }
}
