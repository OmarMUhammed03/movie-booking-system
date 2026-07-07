import { Component, computed, inject, signal } from '@angular/core';
import { Movie } from '../../../core/models/movie.model';
import { MovieService } from '../../../core/services/movie.service';
import { MovieCardComponent } from '../movie-card/movie-card.component';

@Component({
  selector: 'app-movies-list',
  imports: [MovieCardComponent],
  templateUrl: './movies-list.component.html'
})
export class MoviesListComponent {
  private movieService = inject(MovieService);

  resource = this.movieService.moviesResource;
  searchQuery = signal('');
  selectedGenre = signal('All');

  availableGenres = computed((): string[] => {
    const movies = this.movies();
    const genres = new Set(
      movies
        .map((movie) => movie.genre)
        .filter((genre): genre is string => Boolean(genre))
    );
    return ['All', ...Array.from(genres).sort()];
  });

  filteredMovies = computed((): Movie[] => {
    const movies = this.movies();
    const query = this.searchQuery().toLowerCase().trim();
    const genre = this.selectedGenre();

    return movies.filter((movie) => {
      const matchesGenre =
        genre === 'All' || movie.genre?.toLowerCase() === genre.toLowerCase();
      const matchesSearch =
        !query ||
        movie.title.toLowerCase().includes(query) ||
        movie.genre?.toLowerCase().includes(query) ||
        movie.description?.toLowerCase().includes(query);

      return matchesGenre && matchesSearch;
    });
  });

  onSearchInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchQuery.set(value);
  }

  selectGenre(genre: string): void {
    this.selectedGenre.set(genre);
  }

  private movies(): Movie[] {
    const value = this.resource.value();
    return Array.isArray(value) ? value : [];
  }
}
