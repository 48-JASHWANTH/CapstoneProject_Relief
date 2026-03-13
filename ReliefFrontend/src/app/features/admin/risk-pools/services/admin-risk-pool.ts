import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RiskPoolResponse, RiskPoolRequest } from '../../../../core/models/risk-pool.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

@Injectable({ providedIn: 'root' })
export class AdminRiskPoolService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/risk-pools`;

  getAll(): Observable<RiskPoolResponse[]> {
    return this.http.get<RiskPoolResponse[]>(this.BASE);
  }

  create(req: RiskPoolRequest): Observable<RiskPoolResponse> {
    return this.http.post<RiskPoolResponse>(this.BASE, req);
  }

  update(id: number, req: RiskPoolRequest): Observable<RiskPoolResponse> {
    return this.http.put<RiskPoolResponse>(`${this.BASE}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE}/${id}`);
  }

  getCritical(): Observable<RiskPoolResponse[]> {
    return this.http.get<RiskPoolResponse[]>(`${this.BASE}/critical`);
  }

  evaluateThreshold(id: number): Observable<RiskPoolResponse> {
    return this.http.patch<RiskPoolResponse>(`${this.BASE}/${id}/evaluate-threshold`, {});
  }

  getDisasterTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.BASE}/disaster-types`);
  }
}
