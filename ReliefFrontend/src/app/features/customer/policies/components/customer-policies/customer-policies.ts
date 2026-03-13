import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerPolicyService, PolicyResponse } from '../../services/customer-policy';
import { CustomerPaymentService } from '../../../payments/services/customer-payment';
import { PayPremiumDialog } from '../pay-premium-dialog/pay-premium-dialog';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-customer-policies',
  imports: [CommonModule, FormsModule, RouterLink, PayPremiumDialog],
  templateUrl: './customer-policies.html',
  styleUrl: './customer-policies.css',
})
export class CustomerPolicies implements OnInit {
  private svc = inject(CustomerPolicyService);
  private paymentSvc = inject(CustomerPaymentService);
  private router = inject(Router);
  private auth = inject(AuthService);
  all = signal<PolicyResponse[]>([]);
  filtered = signal<PolicyResponse[]>([]);
  loading = signal(true);
  successMsg = signal('');
  filterStatus = 'ALL';
  statuses = ['ALL', 'PENDING', 'APPROVED', 'ACTIVE', 'EXPIRED', 'REJECTED'];
  showPayDialog = signal(false);
  selectedPolicy = signal<PolicyResponse | null>(null);
  userId = this.auth.getUserId();

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.getMyPolicies(this.userId).subscribe(p => {
      this.all.set(p);
      this.applyFilter();
      this.loading.set(false);
    });
  }

  applyFilter() {
    this.filtered.set(this.filterStatus === 'ALL'
      ? this.all()
      : this.all().filter(p => p.status === this.filterStatus));
  }

  openPay(p: PolicyResponse) {
    this.selectedPolicy.set(p);
    this.showPayDialog.set(true);
  }

  onPayConfirmed() {
    this.paymentSvc.payPremium(this.userId, this.selectedPolicy()!.id).subscribe(() => {
      this.showPayDialog.set(false);
      this.load();
      this.toast('Premium paid successfully! Policy is now ACTIVE');
    });
  }

  statusClass(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'bg-yellow-100 text-yellow-700',
      APPROVED: 'bg-[#F3F4F4] text-[#612D53]',
      ACTIVE: 'bg-green-100 text-green-700',
      EXPIRED: 'bg-gray-100 text-gray-600',
      REJECTED: 'bg-red-100 text-red-700',
    };
    return m[s] ?? 'bg-gray-100 text-gray-600';
  }

  toast(msg: string) {
    this.successMsg.set(msg);
    setTimeout(() => this.successMsg.set(''), 4000);
  }
}

