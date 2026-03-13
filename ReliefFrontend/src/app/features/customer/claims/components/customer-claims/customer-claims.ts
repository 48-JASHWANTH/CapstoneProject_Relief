import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerClaimsService, CustomerClaimResponse } from '../../services/customer-claims';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-customer-claims',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './customer-claims.html',
  styleUrl: './customer-claims.css',
})
export class CustomerClaims implements OnInit {
  private svc = inject(CustomerClaimsService);
  private router = inject(Router);
  private auth = inject(AuthService);
  all = signal<CustomerClaimResponse[]>([]);
  filtered = signal<CustomerClaimResponse[]>([]);
  loading = signal(true);
  successMsg = signal('');
  filterStatus = 'ALL';
  statuses = ['ALL', 'FILED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'PAID'];
  userId = this.auth.getUserId();

  ngOnInit() {
    const msg = localStorage.getItem('relief_claim_success');
    if (msg) {
      this.successMsg.set(msg);
      localStorage.removeItem('relief_claim_success');
    }
    this.load();
  }

  dismissSuccess() { this.successMsg.set(''); }

  load() {
    this.loading.set(true);
    this.svc.getMyClaims(this.userId).subscribe(c => {
      this.all.set(c);
      this.applyFilter();
      this.loading.set(false);
    });
  }

  applyFilter() {
    this.filtered.set(this.filterStatus === 'ALL'
      ? this.all()
      : this.all().filter(c => c.status === this.filterStatus));
  }

  statusClass(s: string): string {
    const m: Record<string, string> = {
      FILED: 'bg-[#F3F4F4] text-[#612D53]',
      SURVEY_ASSIGNED: 'bg-[#F3F4F4] text-[#612D53]',
      UNDER_REVIEW: 'bg-[#F3F4F4] text-[#612D53]',
      APPROVED: 'bg-green-100 text-green-700',
      REJECTED: 'bg-red-100 text-red-700',
      PAID: 'bg-[#F3F4F4] text-[#612D53]',
    };
    return m[s] ?? 'bg-gray-100 text-gray-600';
  }
}

