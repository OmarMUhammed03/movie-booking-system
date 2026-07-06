import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';

let refreshInProgress = false;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const authService = inject(AuthService);

  const accessToken = tokenStorage.accessToken;

  const authReq = accessToken
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${accessToken}`
        }
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      const isAuthEndpoint =
        req.url.includes('/auth/login') ||
        req.url.includes('/auth/google') ||
        req.url.includes('/auth/register') ||
        req.url.includes('/auth/refresh');

      if (
        error.status !== 401 ||
        isAuthEndpoint ||
        refreshInProgress ||
        !tokenStorage.refreshToken
      ) {
        return throwError(() => error);
      }

      refreshInProgress = true;

      return authService.refresh().pipe(
        switchMap((response) => {
          refreshInProgress = false;

          const retried = req.clone({
            setHeaders: {
              Authorization: `Bearer ${response.accessToken}`
            }
          });

          return next(retried);
        }),
        catchError((refreshError) => {
          refreshInProgress = false;
          tokenStorage.clear();
          return throwError(() => refreshError);
        })
      );
    })
  );
};