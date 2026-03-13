import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { RequestStateService } from '../services/request-state.service';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const requestState = inject(RequestStateService);

  requestState.loading.set(true);
  requestState.error.set('');
  requestState.success.set('');

  return next(req).pipe(
    finalize(() => requestState.loading.set(false)),
    catchError(err => {
      const msg =
        err.error?.error ||
        err.error?.message ||
        err.message ||
        'Something went wrong. Please try again.';
      requestState.error.set(msg);
      return throwError(() => err);
    }),
  );
};
