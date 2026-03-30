import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth';

export interface ChatRequest {
  message: string;
  conversationId?: string;
  userId?: number;
  userRole?: string;
}

export interface ChatResponse {
  response: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = 'http://localhost:8080/api/chat';

  sendMessage(request: ChatRequest): Observable<ChatResponse> {
    // Enrich with auth info when logged in
    if (this.authService.isLoggedIn()) {
      request.userId   = this.authService.getUserId();
      request.userRole = this.authService.getRole() ?? undefined;
    }

    // Attach Bearer token so the JwtFilter can authenticate the request
    const token = this.authService.getToken();
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    return this.http.post<ChatResponse>(this.apiUrl, request, { headers });
  }
}
