import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse, UserStatusRequest, AssignRolesRequest } from '../../../../core/models/user.model';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  roleName: string;
  licenseNumber?: string;
  region?: string;
}

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private http = inject(HttpClient);
  private BASE = `${API_BASE_URL}/api/admin/users`;

  getAll(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(this.BASE);
  }

  create(req: CreateUserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.BASE, req);
  }

  updateStatus(id: number, req: UserStatusRequest): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.BASE}/${id}/status`, req);
  }

  assignRoles(req: AssignRolesRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.BASE}/assign-roles`, req);
  }

  removeRole(id: number, roleName: string): Observable<UserResponse> {
    return this.http.delete<UserResponse>(`${this.BASE}/${id}/roles/${roleName}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE}/${id}`);
  }

  getByRole(roleName: string): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.BASE}/by-role`, { params: new HttpParams().set('roleName', roleName) });
  }

  getByStatus(status: string): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.BASE}/by-status`, { params: new HttpParams().set('status', status) });
  }
}
