import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PolicyResponse } from '../../policies/services/customer-policy';
import { CustomerClaimResponse } from '../../claims/services/customer-claims';
import { PaymentResponse } from '../../payments/services/customer-payment';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface UserDashboardResponse {
  userName: string;
  totalPolicies: number;
  activePolicies: number;
  pendingPolicies: number;
  totalClaims: number;
  pendingClaims: number;
  totalPremiumPaid: number;
  recentPolicies: PolicyResponse[];
  recentClaims: CustomerClaimResponse[];
  recentPayments: PaymentResponse[];
}

@Injectable({ providedIn: 'root' })
export class CustomerDashboardService {
  private http = inject(HttpClient);

  getDashboard(userId: number): Observable<UserDashboardResponse> {
    return this.http.get<any>(`${API_BASE_URL}/api/users/${userId}/dashboard`).pipe(
      map(r => ({
        userName: r.name,
        totalPolicies: r.totalPolicies,
        activePolicies: r.activePolicies,
        pendingPolicies: r.pendingPolicies,
        totalClaims: r.totalClaims,
        pendingClaims: r.pendingClaims,
        totalPremiumPaid: r.totalPremiumPaid ?? 0,
        recentPolicies: (r.policies ?? []).slice(0, 5),
        recentClaims: (r.claims ?? []).slice(0, 5),
        recentPayments: (r.payments ?? []).slice(0, 5),
      }))
    );
  }
}

