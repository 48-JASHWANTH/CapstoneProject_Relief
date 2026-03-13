import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DisasterZoneResponse, DisasterZoneRequest } from '../../../../core/models/disaster-zone.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

@Injectable({ providedIn: 'root' })
export class AdminDisasterZoneService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/disaster-zones`;

  getAll(): Observable<DisasterZoneResponse[]> {
    return this.http.get<DisasterZoneResponse[]>(this.BASE);
  }

  create(req: DisasterZoneRequest): Observable<DisasterZoneResponse> {
    return this.http.post<DisasterZoneResponse>(this.BASE, req);
  }

  update(id: number, req: DisasterZoneRequest): Observable<DisasterZoneResponse> {
    return this.http.put<DisasterZoneResponse>(`${this.BASE}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE}/${id}`);
  }

  getByRiskLevel(level: string): Observable<DisasterZoneResponse[]> {
    return this.http.get<DisasterZoneResponse[]>(`${this.BASE}/by-risk-level`, { params: new HttpParams().set('riskLevel', level) });
  }

  getByDisasterType(type: string): Observable<DisasterZoneResponse[]> {
    return this.http.get<DisasterZoneResponse[]>(`${this.BASE}/by-disaster-type`, { params: new HttpParams().set('disasterType', type) });
  }
}
