import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PolicyResponse, PolicyApprovalRequest } from '../../../../core/models/policy.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface AssignAgentRequest {
  agentId: number;
}

@Injectable({ providedIn: 'root' })
export class AdminPolicyService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/policies`;

  getAll(): Observable<PolicyResponse[]> {
    return this.http.get<PolicyResponse[]>(this.BASE);
  }

  getById(id: number): Observable<PolicyResponse> {
    return this.http.get<PolicyResponse>(`${this.BASE}/${id}`);
  }

  approveOrReject(id: number, req: PolicyApprovalRequest): Observable<PolicyResponse> {
    return this.http.patch<PolicyResponse>(`${this.BASE}/${id}/approval`, req);
  }

  assignAgent(policyId: number, req: AssignAgentRequest): Observable<PolicyResponse> {
    return this.http.patch<PolicyResponse>(`${this.BASE}/${policyId}/assign-agent`, req);
  }

  getByStatus(status: string): Observable<PolicyResponse[]> {
    return this.http.get<PolicyResponse[]>(`${this.BASE}/by-status`, { params: new HttpParams().set('status', status) });
  }

  getByDisasterType(type: string): Observable<PolicyResponse[]> {
    return this.http.get<PolicyResponse[]>(`${this.BASE}/by-disaster-type`, { params: new HttpParams().set('disasterType', type) });
  }
}

