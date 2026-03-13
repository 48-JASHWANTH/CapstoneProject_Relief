import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const required = route.data['role'] as string;
  const current = authService.getRole();
  if (current !== required) {
    router.parseUrl('/home');
    return false;
  }
  return true;
};
