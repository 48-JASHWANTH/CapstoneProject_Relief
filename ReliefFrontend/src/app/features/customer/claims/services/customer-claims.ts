import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface CustomerClaimResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  disasterType: string | null;
  propertyAddress: string | null;
  description: string;
  estimatedLoss: number;
  approvedAmount: number | null;
  status: string;
  officerRemarks: string | null;
  filedDate: string;
  resolvedDate: string | null;
}

export interface ClaimRequest {
  policyId: number;
  description: string;
  estimatedLoss: number;
}

@Injectable({ providedIn: 'root' })
export class CustomerClaimsService {
  private http = inject(HttpClient);
  private readonly BASE = API_BASE_URL;

  getMyClaims(userId: number): Observable<CustomerClaimResponse[]> {
    return this.http.get<CustomerClaimResponse[]>(`${this.BASE}/api/users/${userId}/claims`);
  }

  getById(userId: number, claimId: number): Observable<CustomerClaimResponse> {
    return this.http.get<CustomerClaimResponse>(`${this.BASE}/api/users/${userId}/claims/${claimId}`);
  }

  fileClaim(userId: number, req: ClaimRequest): Observable<CustomerClaimResponse> {
    return this.http.post<CustomerClaimResponse>(`${this.BASE}/api/users/${userId}/claims`, req);
  }
}

