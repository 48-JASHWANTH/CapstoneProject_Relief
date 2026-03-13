import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from '../../../../core/models/user.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface OfficerClaimSummary {
  id: number;
  claimNumber: string;
  policyNumber: string;
  disasterType: string;
  status: string;
  filedDate: string;
  estimatedLoss: number;
  userName: string;
}

@Injectable({ providedIn: 'root' })
export class AdminClaimsOfficerService {
  private http = inject(HttpClient);
  private USERS_BASE = `${API_BASE_URL}/api/admin/users`;
  private CLAIMS_BASE = `${API_BASE_URL}/api/admin/claims`;

  getOfficers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.USERS_BASE}/by-role`, { params: new HttpParams().set('roleName', 'CLAIMS_OFFICER') });
  }

  getClaimsByOfficer(officerId: number): Observable<OfficerClaimSummary[]> {
    return this.http.get<OfficerClaimSummary[]>(this.CLAIMS_BASE);
  }
}
