import { Component, Input, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerClaimsService, CustomerClaimResponse } from '../../services/customer-claims';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-customer-claim-detail',
  imports: [CommonModule],
  templateUrl: './customer-claim-detail.html',
  styleUrl: './customer-claim-detail.css',
})
export class CustomerClaimDetail implements OnInit {
  @Input() id!: string;
  private svc = inject(CustomerClaimsService);
  private router = inject(Router);
  private auth = inject(AuthService);
  claim = signal<CustomerClaimResponse | null>(null);
  loading = signal(true);
  userId = this.auth.getUserId();

  ngOnInit() {
    this.svc.getById(this.userId, Number(this.id)).subscribe(c => {
      this.claim.set(c ?? null);
      this.loading.set(false);
    });
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

  viewDocument(fileUrl: string) {
    this.svc.downloadDocument(fileUrl).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: () => console.error('Failed to download document')
    });
  }

  back() { this.router.navigate(['/customer/claims']); }
}

