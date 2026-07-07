import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'movies',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./feature/auth/login/login.component').then(
        (m) => m.LoginComponent
      )
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./feature/auth/register/register.component').then(
        (m) => m.RegisterComponent
      )
  },
  {
    path: 'reservations',
    // canActivate: [authGuard],
    loadComponent: () =>
      import('./feature/reservations/reservations-list/reservations-list.component').then(
        (m) => m.ReservationsListComponent
      )
  },
  {
    path: 'movies',
    loadComponent: () =>
      import('./feature/movies/movies-list/movies-list.component').then(
        (m) => m.MoviesListComponent
      )
  },
  {
    path: 'movies/:id',
    loadComponent: () =>
      import('./feature/movies/movie-details/movie-details.component').then(
        (m) => m.MovieDetailsComponent
      )
  }
];
