import { HttpClient } from '@angular/common/http';
import { inject, Injectable, resource } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { Movie, PageResponse } from '../models/movie.model';

@Injectable({ providedIn: 'root' })
export class MovieService {
  private http = inject(HttpClient);

  moviesResource = resource({
    loader: async (): Promise<Movie[]> => {
      const response = await firstValueFrom(
        this.http.get<PageResponse<Movie>>(`${environment.movieUrl}/movies`, {
          params: { page: 0, size: 50, sort: 'title,asc' }
        })
      );
      return response.content;
    }
  });

  getMovieById(id: string): Promise<Movie> {
    return firstValueFrom(
      this.http.get<Movie>(`${environment.movieUrl}/movies/${id}`)
    );
  }
}
