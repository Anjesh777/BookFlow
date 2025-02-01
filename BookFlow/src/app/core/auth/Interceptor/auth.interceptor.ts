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
      '/all/getListOFDistrict'
  ];

  if (publicEndpoints.some(endpoint => req.url.includes(endpoint))) {
      return next(req);
  }


  const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
  });

  return next(req.clone({ headers }));
};