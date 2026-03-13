import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface RiskPoolSnapshot {
  disasterType: string;
  premiumCollected: number;
  claimsPaid: number;
  status: string;
  critical: boolean;
}

export interface PendingClaim {
  id: number;
  claimNumber: string;
  policyNumber: string;
  estimatedLoss: number;
  status: string;
  filedDate: string;
}

export interface DashboardClaim {
  disasterType: string;
  count: number;
}

export interface ClaimsOfficerDashboardResponse {
  officerName: string;
  totalClaims: number;
  filedClaims: number;
  underReview: number;
  approvedClaims: number;
  rejectedClaims: number;
  paidClaims: number;
  totalApprovedAmount: number;
  claimsByDisasterType: DashboardClaim[];
  claimsByStatus: { status: string; count: number }[];
  riskPools: RiskPoolSnapshot[];
  pendingAttention: PendingClaim[];
}

@Injectable({ providedIn: 'root' })
export class ClaimsOfficerDashboardService {
  private http = inject(HttpClient);

  getDashboard(officerId: number): Observable<ClaimsOfficerDashboardResponse> {
    return this.http.get<ClaimsOfficerDashboardResponse>(`${API_BASE_URL}/api/claims-officer/${officerId}/dashboard`);
  }
}
