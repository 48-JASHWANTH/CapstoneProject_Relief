import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PolicyResponse } from '../../../../core/models/policy.model';
import { ClaimResponse } from '../../../../core/models/claim.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface AgentDashboardResponse {
  agentName: string;
  licenseNumber: string;
  region: string;
  totalPoliciesAssigned: number;
  pendingPolicies: number;
  approvedPolicies: number;
  activePolicies: number;
  rejectedPolicies: number;
  expiredPolicies: number;
  pendingClaims: number;
  policiesByDisasterType: { [key: string]: number };
  lossFrequencyByDisasterType: { [key: string]: number };
  approvalRatio: number;
  recentPolicies: PolicyResponse[];
  recentClaims: ClaimResponse[];
}

@Injectable({ providedIn: 'root' })
export class AgentDashboardService {
  private http = inject(HttpClient);

  getDashboard(agentId: number): Observable<AgentDashboardResponse> {
    return this.http.get<AgentDashboardResponse>(`${API_BASE_URL}/api/agents/${agentId}/dashboard`);
  }
}
