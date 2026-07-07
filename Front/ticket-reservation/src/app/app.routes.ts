import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
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
    canActivate: [authGuard],
    loadComponent: () =>
      import('./feature/reservations/reservations-list/reservations-list.component').then(
        (m) => m.ReservationsListComponent
      )
  },
  {
    path: 'shows/:showId/seats',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./feature/seat-selection/seat-selection.component').then(
        (m) => m.SeatSelectionComponent
      )
  }
];