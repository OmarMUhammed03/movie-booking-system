import { DatePipe } from '@angular/common';
import { Component, inject, resource } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map } from 'rxjs';
import { MovieService } from '../../../core/services/movie.service';

@Component({
  selector: 'app-movie-details',
  imports: [RouterLink, DatePipe],
  templateUrl: './movie-details.component.html'
})
export class MovieDetailsComponent {
  private route = inject(ActivatedRoute);
  private movieService = inject(MovieService);

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

  posterUrl(): string {
    const movie = this.resource.value();
    return movie?.posterUrl || 'https://picsum.photos/seed/movie-placeholder/500/750';
  }
}
