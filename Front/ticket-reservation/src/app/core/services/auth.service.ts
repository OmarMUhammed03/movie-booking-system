import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // TEMP STUB: replace with real token/session handling once auth-service login flow is wired up.
  private readonly userIdSignal = signal<string>('11111111-1111-1111-1111-111111111111');

  getUserId(): string {
    return this.userIdSignal();
  }
}