import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'reservations',
    pathMatch: 'full'
  },
  {
    path: 'reservations',
    loadComponent: () =>
      import('./feature/reservations/reservations-list/reservations-list.component')
        .then((m) => m.ReservationsListComponent)
  }
];