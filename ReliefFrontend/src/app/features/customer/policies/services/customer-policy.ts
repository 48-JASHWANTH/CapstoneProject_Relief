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
  yearBuilt?: number;
  roofAge?: number;
  constructionMaterial?: string;
  previousClaimsHistory?: string;
  safetyFeatures?: string;
  documents?: PolicyDocumentResponse[];
}

export interface PolicyDocumentResponse {
  id: number;
  policyId: number;
  documentType: string;
  fileUrl: string;
  documentStatus: string;
  agentRemarks: string | null;
  uploadedAt: string;
}

export interface PolicyAdvancedDetailsRequest {
  yearBuilt: number;
  roofAge: number;
  constructionMaterial: string;
  previousClaimsHistory: string;
  safetyFeatures: string;
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

  uploadDocument(userId: number, policyId: number, documentType: string, file: File): Observable<PolicyDocumentResponse> {
    const formData = new FormData();
    formData.append('documentType', documentType);
    formData.append('file', file);
    return this.http.post<PolicyDocumentResponse>(`${this.BASE}/api/users/${userId}/policies/${policyId}/documents`, formData);
  }

  submitAdvancedDetails(userId: number, policyId: number, request: PolicyAdvancedDetailsRequest): Observable<PolicyResponse> {
    return this.http.put<PolicyResponse>(`${this.BASE}/api/users/${userId}/policies/${policyId}/advanced-details`, request);
  }

  downloadDocument(fileUrl: string): Observable<Blob> {
    return this.http.get(`${this.BASE}/api/documents/${fileUrl}`, { responseType: 'blob' });
  }
}

