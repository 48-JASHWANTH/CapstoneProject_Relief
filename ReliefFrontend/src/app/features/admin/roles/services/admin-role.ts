import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { RoleResponse, RoleRequest } from '../../../../core/models/role.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

@Injectable({ providedIn: 'root' })
export class AdminRoleService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/roles`;

  getAll(): Observable<RoleResponse[]> {
    return this.http.get<RoleResponse[]>(this.BASE);
  }

  create(req: RoleRequest): Observable<RoleResponse> {
    return this.http.post<RoleResponse>(this.BASE, req);
  }

  update(id: number, req: RoleRequest): Observable<RoleResponse> {
    return this.http.put<RoleResponse>(`${this.BASE}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE}/${id}`);
  }

  getRoleNames(): Observable<string[]> {
    return this.getAll().pipe(map(roles => roles.map(r => r.name)));
  }
}
