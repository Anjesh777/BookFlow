import {HttpHeaders, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../service/auth.service';


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  const publicEndpoints = [
      '/all/login',
      '/all/registercmp',
      '/all/resend-token',
      '/all/set-password',
      '/all/forgot',
      '/all/getListOFDistrict',
      '/verification/resend-token',
      '/all/reset'

  ];

  if (req.body instanceof FormData) {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}` 
    });

    return next(req.clone({ headers }));
  } else {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json', 
      'Authorization': `Bearer ${token}`
    });

  return next(req.clone({ headers }));
}};