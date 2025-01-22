import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';


export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (isPlatformServer(platformId)) {
    return true;
  }


  console.log('Auth Guard - Checking authentication');


  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/login-cmp']);
  } 

  const userRole = authService.getUserRole();
  const requiredRoles = route.data['roles'] as string[];

  if (!requiredRoles || requiredRoles.length === 0) {
    console.log('Auth Guard - No roles required');

    return true;
  }

  if (userRole && requiredRoles.includes(userRole)) {
    console.log('Auth Guard - User has required role');
    return true;
  }



  console.log('Auth Guard - Access denied');
  return router.createUrlTree(['/login-cmp']);

};
