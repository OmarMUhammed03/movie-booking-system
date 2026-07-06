export type ReservationStatus = 'CONFIRMED' | 'PENDING' | 'CANCELLED';

export interface Reservation {
  id: string;               
  movieTitle: string;
  moviePosterUrl: string;
  status: ReservationStatus;
  showDateTime: Date | string; 
  location: string;       
  totalPrice: number;
}