import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { API_BASE_URL } from '../constants/api.constants';

export interface JwtRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  id: number;
  name: string;
  email: string;
  status: string;
  createdAt: string;
  role: string;
}

export interface LoginResponse {
  token: string;
}

interface TokenPayload {
  sub: string;
  role: string;
  userId: number;
  iat: number;
  exp: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private readonly BASE = API_BASE_URL;

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.BASE}/login`, { email, password }).pipe(
      tap((res: LoginResponse) => {
        localStorage.setItem('relief_token', res.token);
      })
    );
  }

  register(req: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.BASE}/register`, req);
  }

  logout(): void {
    localStorage.removeItem('relief_token');
    this.router.navigate(['/home']);
  }

  getToken(): string | null {
    return localStorage.getItem('relief_token');
  }

  private decodeToken(): TokenPayload | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return jwtDecode<TokenPayload>(token);
    } catch {
      return null;
    }
  }

  getRole(): string | null {
    return this.decodeToken()?.role ?? null;
  }

  getUserEmail(): string {
    return this.decodeToken()?.sub ?? '';
  }

  getUserId(): number {
    return this.decodeToken()?.userId ?? 0;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
