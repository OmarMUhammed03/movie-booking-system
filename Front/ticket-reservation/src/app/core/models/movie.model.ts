export interface Movie {
  id: string;
  title: string;
  description: string;
  genre: string;
  durationMinutes: number;
  posterUrl: string;
  releaseDate: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
