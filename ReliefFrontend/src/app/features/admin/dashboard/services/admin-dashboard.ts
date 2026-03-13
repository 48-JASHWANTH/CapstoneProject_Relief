import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface RiskPoolSummaryItem {
  disasterType: string;
  totalPremiumCollected: number;
  totalClaimsPaid: number;
  poolStatus: string;
  criticalFlag: boolean;
}

export interface AdminDashboardResponse {
  totalUsers: number;
  totalAgents: number;
  totalPolicies: number;
  totalClaims: number;
  totalPayments: number;
  totalDisasterZones: number;
  totalRiskPools: number;
  criticalRiskPools: number;
  activePolicies: number;
  pendingPolicies: number;
  approvedClaims: number;
  pendingClaims: number;
  policiesByDisasterType: { [key: string]: number };
  claimsByStatus: { [key: string]: number };
  riskPoolSummary: RiskPoolSummaryItem[];
}

@Injectable({ providedIn: 'root' })
export class AdminDashboardService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/dashboard`;

  getDashboard(): Observable<AdminDashboardResponse> {
    return this.http.get<AdminDashboardResponse>(this.BASE);
  }
}
