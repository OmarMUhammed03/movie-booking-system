import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable, catchError, finalize, shareReplay, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';
import { AuthResponse } from '../models/auth.model';

// Module-scoped so it's shared across every request through this interceptor.
// Concurrent 401s all subscribe to the SAME in-flight refresh call (via shareReplay)
// instead of each racing/failing independently.
let refreshInFlight$: Observable<AuthResponse> | null = null;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const authService = inject(AuthService);

  const accessToken = tokenStorage.accessToken;
  const authReq = accessToken
    ? req.clone({ setHeaders: { Authorization: `Bearer ${accessToken}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthEndpoint =
        req.url.includes('/auth/login') ||
        req.url.includes('/auth/google') ||
        req.url.includes('/auth/register') ||
        req.url.includes('/auth/refresh');

      if (error.status !== 401 || isAuthEndpoint || !tokenStorage.refreshToken) {
        return throwError(() => error);
      }

      // Start a refresh only if one isn't already running; every caller that
      // arrives while it's in flight reuses the same observable via shareReplay.
      if (!refreshInFlight$) {
        refreshInFlight$ = authService.refresh().pipe(
          shareReplay({ bufferSize: 1, refCount: true }),
          finalize(() => (refreshInFlight$ = null))
        );
      }

      return refreshInFlight$.pipe(
        switchMap((response) => {
          const retried = req.clone({
            setHeaders: { Authorization: `Bearer ${response.accessToken}` }
          });
          return next(retried);
        }),
        catchError((refreshError) => {
          tokenStorage.clear();
          return throwError(() => refreshError);
        })
      );
    })
  );
};
