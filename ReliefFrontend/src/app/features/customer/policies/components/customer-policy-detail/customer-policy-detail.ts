import { Component, Input, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerPolicyService, PolicyResponse } from '../../services/customer-policy';
import { CustomerPaymentService } from '../../../payments/services/customer-payment';
import { PayPremiumDialog } from '../pay-premium-dialog/pay-premium-dialog';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-customer-policy-detail',
  imports: [CommonModule, PayPremiumDialog],
  templateUrl: './customer-policy-detail.html',
  styleUrl: './customer-policy-detail.css',
})
export class CustomerPolicyDetail implements OnInit {
  @Input() id!: string;
  private svc = inject(CustomerPolicyService);
  private paymentSvc = inject(CustomerPaymentService);
  private router = inject(Router);
  private auth = inject(AuthService);
  policy = signal<PolicyResponse | null>(null);
  loading = signal(true);
  successMsg = signal('');
  showPayDialog = signal(false);
  userId = this.auth.getUserId();

  ngOnInit() {
    this.svc.getById(this.userId, Number(this.id)).subscribe(p => {
      this.policy.set(p ?? null);
      this.loading.set(false);
    });
  }

  onPayConfirmed() {
    this.paymentSvc.payPremium(this.userId, this.policy()!.id).subscribe(() => {
      this.policy.update(p => p ? { ...p, status: 'ACTIVE' } : p);
      this.showPayDialog.set(false);
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

  back() { this.router.navigate(['/customer/policies']); }

  toast(msg: string) {
    this.successMsg.set(msg);
    setTimeout(() => this.successMsg.set(''), 4000);
  }
}

