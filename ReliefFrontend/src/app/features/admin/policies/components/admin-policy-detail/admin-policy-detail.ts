import { Component, OnInit, Input, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AdminPolicyService } from '../../services/admin-policy';
import { PolicyResponse } from '../../../../../core/models/policy.model';

@Component({
  selector: 'app-admin-policy-detail',
  imports: [CommonModule],
  templateUrl: './admin-policy-detail.html',
  styleUrl: './admin-policy-detail.css',
})
export class AdminPolicyDetail implements OnInit {
  @Input() id!: string;
  private svc = inject(AdminPolicyService);
  private router = inject(Router);
  policy = signal<PolicyResponse | null>(null);
  loading = signal(true);

  ngOnInit(): void {
    this.svc.getById(Number(this.id)).subscribe(p => {
      this.policy.set(p || null);
      this.loading.set(false);
    });
  }

  goBack(): void { this.router.navigate(['/admin/policies']); }

  statusClass(status: string): string {
    const map: Record<string, string> = {
      ACTIVE: 'bg-green-100 text-green-700',
      APPROVED: 'bg-green-100 text-green-700',
      PENDING: 'bg-amber-100 text-amber-700',
      UNDER_REVIEW: 'bg-yellow-100 text-yellow-700',
      FORWARDED: 'bg-[#F3F4F4] text-[#612D53]',
      REJECTED: 'bg-red-100 text-red-700',
      EXPIRED: 'bg-gray-100 text-gray-500',
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  formatCurrency(v: number): string { return '₹' + v.toLocaleString(); }
}
