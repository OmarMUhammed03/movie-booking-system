export type TicketStatus = 'AVAILABLE' | 'RESERVED' | 'BOOKED';

export interface ShowDetails {
  id: string;
  movieId: string;
  hallId: string;
  startTime: string; // ISO 8601 datetime string
  endTime: string;
  price: number;
}

export interface MovieDetails {
  id: string;
  title: string;
  posterUrl: string;
}

export interface HallDetails {
  id: string;
  name: string;
  totalSeats: number;
  rowCount: number;
  seatsPerRow: number;
  screenType: string;
}

export interface TicketInfo {
  id: string;
  showId: string;
  seatNumber: string; // e.g. "C4" -> row C, seat 4
  price: number;
  status: TicketStatus;
}
