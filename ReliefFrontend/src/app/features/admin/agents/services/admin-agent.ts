import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AgentResponse, AgentRequest } from '../../../../core/models/agent.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

@Injectable({ providedIn: 'root' })
export class AdminAgentService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/agents`;

  getAll(): Observable<AgentResponse[]> {
    return this.http.get<AgentResponse[]>(this.BASE);
  }

  getByRegion(region: string): Observable<AgentResponse[]> {
    return this.http.get<AgentResponse[]>(`${this.BASE}/by-region`, { params: { region } });
  }

  create(req: AgentRequest): Observable<AgentResponse> {
    return this.http.post<AgentResponse>(this.BASE, req);
  }

  update(id: number, req: AgentRequest): Observable<AgentResponse> {
    return this.http.put<AgentResponse>(`${this.BASE}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE}/${id}`);
  }
}
