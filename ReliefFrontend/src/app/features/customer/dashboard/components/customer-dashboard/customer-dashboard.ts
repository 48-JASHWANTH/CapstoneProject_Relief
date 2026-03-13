import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CustomerDashboardService, UserDashboardResponse } from '../../services/customer-dashboard';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-customer-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './customer-dashboard.html',
  styleUrl: './customer-dashboard.css',
})
export class CustomerDashboard implements OnInit {
  private svc = inject(CustomerDashboardService);
  private router = inject(Router);
  private auth = inject(AuthService);
  dashboard = signal<UserDashboardResponse | null>(null);
  loading = signal(true);
  userId = this.auth.getUserId();

  ngOnInit() {
    this.svc.getDashboard(this.userId).subscribe(d => {
      this.dashboard.set(d);
      this.loading.set(false);
    });
  }

  statusClass(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'bg-yellow-100 text-yellow-700',
      APPROVED: 'bg-[#F3F4F4] text-[#612D53]',
      ACTIVE: 'bg-green-100 text-green-700',
      EXPIRED: 'bg-gray-100 text-gray-600',
      REJECTED: 'bg-red-100 text-red-700',
      FILED: 'bg-[#F3F4F4] text-[#612D53]',
      SURVEY_ASSIGNED: 'bg-purple-100 text-purple-700',
      UNDER_REVIEW: 'bg-amber-100 text-amber-700',
      PAID: 'bg-teal-100 text-teal-700',
      COMPLETED: 'bg-green-100 text-green-700',
      FAILED: 'bg-red-100 text-red-700',
    };
    return m[s] ?? 'bg-gray-100 text-gray-600';
  }
}

