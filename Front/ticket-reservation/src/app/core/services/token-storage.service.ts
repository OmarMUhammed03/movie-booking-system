import { Injectable } from '@angular/core';
import { AuthResponse } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_ID_KEY = 'user_id';
  private readonly ROLE_KEY = 'role';

  saveAuth(response: AuthResponse): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, response.accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    localStorage.setItem(this.USER_ID_KEY, response.userId);

    if (response.role) {
      localStorage.setItem(this.ROLE_KEY, response.role);
    } else {
      localStorage.removeItem(this.ROLE_KEY);
    }
  }

  clear(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_ID_KEY);
    localStorage.removeItem(this.ROLE_KEY);
  }

  get accessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  get refreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  get userId(): string | null {
    return localStorage.getItem(this.USER_ID_KEY);
  }

  get isLoggedIn(): boolean {
    return !!this.accessToken;
  }
}
