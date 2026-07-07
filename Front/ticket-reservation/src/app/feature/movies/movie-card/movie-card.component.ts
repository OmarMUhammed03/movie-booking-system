import { Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';
import { Movie } from '../../../core/models/movie.model';

@Component({
  selector: 'app-movie-card',
  host: {
    class: 'block h-full'
  },
  templateUrl: './movie-card.component.html'
})
export class MovieCardComponent {
  private router = inject(Router);

  movie = input.required<Movie>();

  posterUrl(): string {
    return this.movie().posterUrl || 'https://picsum.photos/seed/movie-placeholder/400/600';
  }

  openDetails(): void {
    void this.router.navigate(['/movies', this.movie().id]);
  }
}
