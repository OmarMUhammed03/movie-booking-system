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
  },
  {
    path: 'shows/:showId/seats',
    loadComponent: () =>
      import('./feature/seat-selection/seat-selection.component').then(
        (m) => m.SeatSelectionComponent
      )
  },
  {
    path: 'checkout/:reservationId',
    loadComponent: () =>
      import('./feature/payment/checkout-processing/checkout-processing.component').then(
        (m) => m.CheckoutProcessingComponent
      )
  },
  {
    path: 'payment/:result',
    loadComponent: () =>
      import('./feature/payment/payment-result/payment-result.component').then(
        (m) => m.PaymentResultComponent
      )
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./feature/profile/profile.component').then(
        (m) => m.ProfileComponent
      )
  }
];
