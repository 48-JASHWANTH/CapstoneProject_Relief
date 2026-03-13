import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface PolicyResponse {
  id: number;
  policyNumber: string;
  userId: number;
  userName: string;
  agentId: number | null;
  agentName: string | null;
  disasterType: string;
  policyType: string;
  propertyAddress: string;
  propertyValue: number;
  sumInsured: number;
  premiumAmount: number;
  status: string;
  startDate: string;
  endDate: string;
  region: string | null;
  tenure: number | null;
  disasterZoneId: number | null;
  disasterZoneName: string | null;
  disasterZoneRiskFactor: number | null;
  remarks: string | null;
  riskPoolDisasterType: string | null;
}

export interface UserPolicyRequest {
  disasterType: string;
  policyType: string;
  propertyAddress: string;
  propertyValue: number;
  sumInsured: number;
  premiumAmount: number;
  region: string;
  tenure: number;
}

export interface PolicyOptionsResponse {
  disasterTypes: string[];
  regions: string[];
  tenureOptions: number[];
}

@Injectable({ providedIn: 'root' })
export class CustomerPolicyService {
  private http = inject(HttpClient);
  private readonly BASE = API_BASE_URL;

  getPolicyOptions(): Observable<PolicyOptionsResponse> {
    return this.http.get<PolicyOptionsResponse>(`${this.BASE}/api/users/policy-options`);
  }

  getMyPolicies(userId: number): Observable<PolicyResponse[]> {
    return this.http.get<PolicyResponse[]>(`${this.BASE}/api/users/${userId}/policies`);
  }

  getById(userId: number, policyId: number): Observable<PolicyResponse> {
    return this.http.get<PolicyResponse>(`${this.BASE}/api/users/${userId}/policies/${policyId}`);
  }

  submitPolicy(userId: number, req: UserPolicyRequest): Observable<PolicyResponse> {
    return this.http.post<PolicyResponse>(`${this.BASE}/api/users/${userId}/policies`, req);
  }
}

