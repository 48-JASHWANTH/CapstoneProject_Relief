import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CustomerPaymentService, PaymentResponse } from '../../services/customer-payment';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-customer-payments',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './customer-payments.html',
  styleUrl: './customer-payments.css',
})
export class CustomerPayments implements OnInit {
  private svc = inject(CustomerPaymentService);
  private auth = inject(AuthService);
  userId = this.auth.getUserId();
  payments = signal<PaymentResponse[]>([]);
  filtered = signal<PaymentResponse[]>([]);
  filterStatus = 'ALL';
  statuses = ['ALL', 'PENDING', 'COMPLETED', 'FAILED'];
  loading = signal(true);

  ngOnInit(): void {
    this.svc.getMyPayments(this.userId).subscribe(data => {
      this.payments.set(data);
      this.applyFilter();
      this.loading.set(false);
    });
  }

  applyFilter() {
    this.filtered.set(this.filterStatus === 'ALL'
      ? [...this.payments()]
      : this.payments().filter(p => p.paymentStatus === this.filterStatus));
  }

  statusClass(s: string): string {
    const map: Record<string, string> = {
      COMPLETED: 'bg-green-100 text-green-700',
      PENDING:   'bg-yellow-100 text-yellow-700',
      FAILED:    'bg-red-100 text-red-700',
    };
    return map[s] ?? 'bg-gray-100 text-gray-600';
  }

  totalPaid(): number {
    return this.payments()
      .filter(p => p.paymentStatus === 'COMPLETED')
      .reduce((sum, p) => sum + p.amount, 0);
  }

  totalPremiumPaid(): number {
    return this.payments()
      .filter(p => p.paymentStatus === 'COMPLETED' && p.paymentType === 'PREMIUM')
      .reduce((sum, p) => sum + p.amount, 0);
  }

  totalClaimsReceived(): number {
    return this.payments()
      .filter(p => p.paymentStatus === 'COMPLETED' && p.paymentType === 'CLAIM_PAYOUT')
      .reduce((sum, p) => sum + p.amount, 0);
  }
}
