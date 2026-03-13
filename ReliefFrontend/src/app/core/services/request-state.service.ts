import { Injectable, signal } from '@angular/core';

/**
 * Shared request state service.
 * `state` is a typed array of 3 signals: [loading, error, success]
 *   state[0] → loading (boolean)
 *   state[1] → error   (string)
 *   state[2] → success (string)
 *
 * Named aliases (loading / error / success) point to the same signals
 * so templates and components can use either style.
 */
@Injectable({ providedIn: 'root' })
export class RequestStateService {
  /** [loading, error, success] */
  readonly state = [
    signal(false),
    signal(''),
    signal(''),
  ] as const;

  readonly loading = this.state[0];
  readonly error   = this.state[1];
  readonly success = this.state[2];

  reset(): void {
    this.loading.set(false);
    this.error.set('');
    this.success.set('');
  }
}
