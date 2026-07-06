export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED';

export interface ShowSummary {
  id: string;
  movieTitle: string;
  posterUrl: string;
  startTime: string; // ISO 8601 datetime string
  hallName: string;
}

export interface ReservationSummary {
  id: string;
  status: ReservationStatus;
  totalPrice: number;
  createdAt: string; // ISO 8601 datetime string
  seatCount: number;
  show: ShowSummary;
}