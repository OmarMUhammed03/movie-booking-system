import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CheckoutSessionRequest {
  reservationId: string;
  userId: string;
  showId: string;
  ticketIds: string[];
  totalPrice: number;
}

export interface CheckoutSessionResponse {
  id: string;
  reservationId: string;
  stripeSessionId: string;
  checkoutUrl: string;
  amountCents: number;
  currency: string;
  status: 'PENDING' | 'SUCCEEDED' | 'FAILED';
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private http = inject(HttpClient);
  private readonly baseUrl = environment.paymentUrl;

  createCheckoutSession(request: CheckoutSessionRequest): Promise<CheckoutSessionResponse> {
    return firstValueFrom(
      this.http.post<CheckoutSessionResponse>(`${this.baseUrl}/checkout`, request)
    );
  }

  simulateSuccessfulPayment(request: CheckoutSessionRequest): Promise<CheckoutSessionResponse> {
    return firstValueFrom(
      this.http.post<CheckoutSessionResponse>(`${this.baseUrl}/mock-success`, request)
    );
  }
}
