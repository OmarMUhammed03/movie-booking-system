import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AuthResponse,
  GoogleLoginRequest,
  LoginRequest,
  RefreshRequest,
  SignUpRequest
} from '../models/auth.model';
import { TokenStorageService } from './token-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly baseUrl = environment.apiUrl;

  register(request: SignUpRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/api/auth/register`, request);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/api/auth/login`, request).pipe(
      tap((response) => this.tokenStorage.saveAuth(response))
    );
  }

  loginWithGoogle(idToken: string): Observable<AuthResponse> {
    const request: GoogleLoginRequest = { idToken };

    return this.http.post<AuthResponse>(`${this.baseUrl}/api/auth/google`, request).pipe(
      tap((response) => this.tokenStorage.saveAuth(response))
    );
  }

  refresh(): Observable<AuthResponse> {
    const refreshToken = this.tokenStorage.refreshToken ?? '';
    const request: RefreshRequest = { refreshToken };

    return this.http.post<AuthResponse>(`${this.baseUrl}/api/auth/refresh`, request).pipe(
      tap((response) => this.tokenStorage.saveAuth(response))
    );
  }

  logout(): Observable<void> {
    const refreshToken = this.tokenStorage.refreshToken;
    this.tokenStorage.clear();

    if (!refreshToken) {
      return new Observable<void>((subscriber) => {
        subscriber.next();
        subscriber.complete();
      });
    }

    return this.http.post<void>(`${this.baseUrl}/api/auth/logout`, { refreshToken });
  }

  isAuthenticated(): boolean {
    return this.tokenStorage.isLoggedIn;
  }

  getUserId(): string {
    return this.tokenStorage.userId ?? '';
  }
  // Retrieves the email (sub) from the active token
  getUserEmail(): string {
    const token = localStorage.getItem('access_token');
    if (!token) return '';
    const payload = this.decodeTokenPayload(token);
    return payload?.sub ?? '';
  }

  // Retrieves the first role from the active token
  getUserRole(): string {
    const token = localStorage.getItem('access_token');
    if (!token) return '';
    const payload = this.decodeTokenPayload(token);
    return payload?.roles && payload.roles.length > 0 ? payload.roles[0] : '';
  }

  // Decodes the base64 payload of a standard JWT token
  private decodeTokenPayload(token: string): any {
    try {
      const payload = token.split('.')[1];
      // Convert standard base64url characters to match base64 standards
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded);
    } catch {
      return null;
    }
  }
}
