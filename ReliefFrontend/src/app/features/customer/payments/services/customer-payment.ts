import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../core/constants/api.constants';

export interface PaymentResponse {
  id: number;
  policyId: number;
  policyNumber: string;
  paymentType: string;
  amount: number;
  paymentStatus: string;
  paymentDate: string;
}

@Injectable({ providedIn: 'root' })
export class CustomerPaymentService {
  private http = inject(HttpClient);
  private readonly BASE = API_BASE_URL;

  getMyPayments(userId: number): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(`${this.BASE}/api/users/${userId}/payments`);
  }

  payPremium(userId: number, policyId: number): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(`${this.BASE}/api/users/${userId}/payments/pay-premium`, { policyId });
  }
}

