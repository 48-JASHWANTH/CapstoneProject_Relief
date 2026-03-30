import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../../core/services/auth';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface ClaimDocumentResponse {
  id: number;
  claimId: number;
  documentType: string;
  fileUrl: string;
  documentStatus: string;
  officerRemarks: string | null;
  uploadedAt: string;
  // Vertex AI Vision analysis
  aiDamageType: string | null;
  aiSeverity: string | null;
  aiConfidence: number | null;
  aiSuggestedLoss: string | null;
  aiSummary: string | null;
}

export interface ClaimResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  disasterType: string;
  description: string;
  estimatedLoss: number;
  incidentDate: string;
  damageType: string;
  approvedAmount: number | null;
  status: string;
  filedDate: string;
  resolvedDate: string | null;
  officerRemarks: string | null;
  propertyAddress: string | null;
  sumInsured: number | null;
  premiumAmount: number | null;
  userName: string | null;
  documents: ClaimDocumentResponse[];
}

export interface ClaimDecisionRequest {
  decision: string;
  approvedAmount: number;
  remarks: string;
}

@Injectable({ providedIn: 'root' })
export class ClaimsOfficerClaimsService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  private get officerId(): number {
    return this.auth.getUserId();
  }

  private get base(): string {
    return `${API_BASE_URL}/api/claims-officer/${this.officerId}/claims`;
  }

  getAll(): Observable<ClaimResponse[]> {
    return this.http.get<ClaimResponse[]>(this.base);
  }

  getById(id: number): Observable<ClaimResponse> {
    return this.http.get<ClaimResponse>(`${this.base}/${id}`);
  }

  getByStatus(status: string): Observable<ClaimResponse[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<ClaimResponse[]>(`${this.base}/by-status`, { params });
  }

  getByDisasterType(type: string): Observable<ClaimResponse[]> {
    const params = new HttpParams().set('disasterType', type);
    return this.http.get<ClaimResponse[]>(`${this.base}/by-disaster-type`, { params });
  }

  markUnderReview(id: number): Observable<ClaimResponse> {
    return this.http.patch<ClaimResponse>(`${this.base}/${id}/under-review`, {});
  }

  decideClaim(id: number, req: ClaimDecisionRequest): Observable<ClaimResponse> {
    return this.http.patch<ClaimResponse>(`${this.base}/${id}/decision`, req);
  }

  getHighValue(threshold: number): Observable<ClaimResponse[]> {
    const params = new HttpParams().set('threshold', String(threshold));
    return this.http.get<ClaimResponse[]>(`${this.base}/high-value`, { params });
  }

  downloadDocument(fileUrl: string): Observable<Blob> {
    return this.http.get(`${API_BASE_URL}/api/documents/${fileUrl}`, { responseType: 'blob' });
  }
}
