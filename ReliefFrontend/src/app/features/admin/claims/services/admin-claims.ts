import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from '../../../../core/models/user.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface AdminClaimSummary {
  id: number;
  claimNumber: string;
  policyNumber: string;
  disasterType: string;
  status: string;
  filedDate: string;
  estimatedLoss: number;
  userName: string;
  region: string;
  assignedOfficerId: number | null;
  assignedOfficerName: string | null;
}

export interface AssignOfficerRequest {
  officerUserId: number;
}

@Injectable({ providedIn: 'root' })
export class AdminClaimsService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/claims`;
  private USERS_BASE = `${API_BASE_URL}/api/admin/users`;

  getAll(): Observable<AdminClaimSummary[]> {
    return this.http.get<AdminClaimSummary[]>(this.BASE);
  }

  getUnassigned(): Observable<AdminClaimSummary[]> {
    return this.http.get<AdminClaimSummary[]>(`${this.BASE}/unassigned`);
  }

  getOfficers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.USERS_BASE}/by-role`, { params: new HttpParams().set('roleName', 'CLAIMS_OFFICER') });
  }

  assignOfficer(claimId: number, req: AssignOfficerRequest): Observable<AdminClaimSummary> {
    return this.http.patch<AdminClaimSummary>(`${this.BASE}/${claimId}/assign-officer`, req);
  }
}
