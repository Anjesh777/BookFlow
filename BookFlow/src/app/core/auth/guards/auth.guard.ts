import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';

export const authGuard: CanActivateFn = (route, state) => {
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);
  const platformId: object = inject(PLATFORM_ID);

  if (isPlatformServer(platformId)) {
    return true;
  }

  console.log('Auth Guard - Checking authentication');

  if (!authService.isLoggedIn()) {
    console.log('Auth Guard - User not logged in');
      
    return router.createUrlTree(['/login']);
  }

  const userRole = authService.getUserRole();
  const requiredRoles = route.data['roles'] as string[];

  if (!requiredRoles || requiredRoles.length === 0) {
    console.log('Auth Guard - No roles required');
    return true;
  }

  if (userRole && requiredRoles.includes(userRole)) {
    console.log('Auth Guard - User has required role:', userRole);
    return true;
  }

  console.log('Auth Guard - Access denied, redirecting based on role:', userRole);
  switch (userRole) {
    case 'COMPANY_SUPERADMIN':
      return router.createUrlTree(['/superadmin']);
    case 'COMPANY_ADMIN':
      return router.createUrlTree(['/admin']);
    case 'COMPANY_USER':
      return router.createUrlTree(['/user']);
    default:
      authService.logout();
      return router.createUrlTree(['/login']);
  }


};