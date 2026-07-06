import { ReservationSummary } from '../models/reservation.model';

export const MOCK_RESERVATIONS: ReservationSummary[] = [
  {
    id: 'a1111111-1111-1111-1111-111111111111',
    status: 'CONFIRMED',
    totalPrice: 29.0,
    createdAt: '2026-06-04T00:00:00',
    seatCount: 2,
    show: {
      id: 's1111111-1111-1111-1111-111111111111',
      movieTitle: 'Neon Horizon',
      posterUrl: 'https://picsum.photos/seed/neon/300/400',
      startTime: '2026-06-04T19:30:00',
      hallName: 'Screen 3'
    }
  },
  {
    id: 'a2222222-2222-2222-2222-222222222222',
    status: 'PENDING',
    totalPrice: 43.5,
    createdAt: '2026-06-05T00:00:00',
    seatCount: 3,
    show: {
      id: 's2222222-2222-2222-2222-222222222222',
      movieTitle: 'The Last Ember',
      posterUrl: 'https://picsum.photos/seed/ember/300/400',
      startTime: '2026-06-05T21:00:00',
      hallName: 'Screen 1'
    }
  },
  {
    id: 'a3333333-3333-3333-3333-333333333333',
    status: 'CANCELLED',
    totalPrice: 14.5,
    createdAt: '2026-06-04T00:00:00',
    seatCount: 1,
    show: {
      id: 's3333333-3333-3333-3333-333333333333',
      movieTitle: 'Midnight Circuit',
      posterUrl: 'https://picsum.photos/seed/circuit/300/400',
      startTime: '2026-06-04T23:15:00',
      hallName: 'Screen 2'
    }
  }
];