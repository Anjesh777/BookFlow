import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  const publicEndpoints = [
    '/login',
    '/register',
    '/resend-token',
    '/set-password'
  ];

  if (publicEndpoints.some(endpoint => req.url.includes(endpoint))) {
    return next(req);
  }

  if (token) {
    const modifiedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(modifiedReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          authService.logout();  
        }
        return throwError(() => error);
      })
    );
  }

  return next(req);
};