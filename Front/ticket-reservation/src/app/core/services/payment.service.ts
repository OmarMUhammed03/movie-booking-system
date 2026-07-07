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
  status: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private http = inject(HttpClient);

  createCheckoutSession(request: CheckoutSessionRequest): Promise<CheckoutSessionResponse> {
    return firstValueFrom(
      this.http.post<CheckoutSessionResponse>(`${environment.paymentUrl}/checkout`, request)
    );
  }
}
