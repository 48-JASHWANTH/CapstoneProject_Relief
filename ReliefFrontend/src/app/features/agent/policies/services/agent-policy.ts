import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PolicyResponse, PolicyDocumentResponse } from '../../../../core/models/policy.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface AgentPremiumAdjustRequest { adjustedSumInsured: number; adjustedPremium: number; remarks: string; }

@Injectable({ providedIn: 'root' })
export class AgentPolicyService {
  private http = inject(HttpClient);
  private readonly BASE = API_BASE_URL;

  getMyPolicies(agentId: number): Observable<PolicyResponse[]> {
    return this.http.get<PolicyResponse[]>(`${this.BASE}/api/agents/${agentId}/policies`);
  }

  getById(agentId: number, policyId: number): Observable<PolicyResponse> {
    return this.http.get<PolicyResponse>(`${this.BASE}/api/agents/${agentId}/policies/${policyId}`);
  }

  adjustPremium(agentId: number, policyId: number, req: AgentPremiumAdjustRequest): Observable<PolicyResponse> {
    return this.http.patch<PolicyResponse>(`${this.BASE}/api/agents/${agentId}/policies/${policyId}/adjust-premium`, req);
  }

  getByStatus(agentId: number, status: string): Observable<PolicyResponse[]> {
    return this.http.get<PolicyResponse[]>(`${this.BASE}/api/agents/${agentId}/policies/by-status`, { params: new HttpParams().set('status', status) });
  }

  calculatePremium(agentId: number, policyId: number, sumInsured: number): Observable<number> {
    return this.http.get<number>(`${this.BASE}/api/agents/${agentId}/policies/${policyId}/calculate-premium`, { params: new HttpParams().set('sumInsured', sumInsured.toString()) });
  }

  reviewDocument(agentId: number, documentId: number, status: string, remarks?: string): Observable<PolicyDocumentResponse> {
    let params = new HttpParams().set('status', status);
    if (remarks) {
      params = params.set('remarks', remarks);
    }
    return this.http.put<PolicyDocumentResponse>(`${this.BASE}/api/agents/${agentId}/documents/${documentId}/review`, null, { params });
  }

  downloadDocument(fileUrl: string): Observable<Blob> {
    return this.http.get(`${this.BASE}/api/documents/${fileUrl}`, { responseType: 'blob' });
  }
}
