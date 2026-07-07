import { DatePipe } from '@angular/common';
import { Component, computed, inject, resource } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map } from 'rxjs';
import { ShowDetails } from '../../../core/models/show.model';
import { MovieService } from '../../../core/services/movie.service';
import { ShowService } from '../../../core/services/show.service';

@Component({
  selector: 'app-movie-details',
  imports: [RouterLink, DatePipe],
  templateUrl: './movie-details.component.html'
})
export class MovieDetailsComponent {
  private route = inject(ActivatedRoute);
  private movieService = inject(MovieService);
  private showService = inject(ShowService);

  private movieId = toSignal(
    this.route.paramMap.pipe(map((params) => params.get('id'))),
    { initialValue: null }
  );

  resource = resource({
    params: () => this.movieId(),
    loader: async ({ params: id }) => {
      if (!id) {
        throw new Error('Movie id is required');
      }
      return this.movieService.getMovieById(id);
    }
  });

  showsResource = resource({
    loader: async () => this.showService.getShows()
  });

  movieShows = computed<ShowDetails[]>(() => {
    const movieId = this.movieId();
    const shows = this.showsResource.value() ?? [];

    if (!movieId) return [];

    return shows
      .filter((show) => show.movieId === movieId)
      .sort(
        (a, b) =>
          new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
      );
  });

  posterUrl(): string {
    const movie = this.resource.value();
    return movie?.posterUrl || 'https://picsum.photos/seed/movie-placeholder/500/750';
  }
}
