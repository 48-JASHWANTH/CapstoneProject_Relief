import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClaimsOfficerDashboardService, ClaimsOfficerDashboardResponse } from '../../services/claims-officer-dashboard';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-claims-officer-dashboard',
  imports: [CommonModule],
  templateUrl: './claims-officer-dashboard.html',
  styleUrl: './claims-officer-dashboard.css',
})
export class ClaimsOfficerDashboard implements OnInit {
  private svc = inject(ClaimsOfficerDashboardService);
  private router = inject(Router);
  private auth = inject(AuthService);
  dashboard = signal<ClaimsOfficerDashboardResponse | null>(null);
  loading = signal(true);

  ngOnInit() {
    const officerId = this.auth.getUserId();
    this.svc.getDashboard(officerId).subscribe(d => {
      this.dashboard.set(d);
      this.loading.set(false);
    });
  }

  get pieSlices(): { disasterType: string; count: number; color: string; pct: number; offset: number }[] {
    if (!this.dashboard()) return [];
    const colors = ['#0ea5e9', '#f59e0b', '#ef4444', '#8b5cf6'];
    const total = this.dashboard()!.claimsByDisasterType.reduce((s, c) => s + c.count, 0) || 1;
    let cumulative = 0;
    return this.dashboard()!.claimsByDisasterType.map((c, i) => {
      const pct = (c.count / total) * 100;
      const offset = cumulative;
      cumulative += pct;
      return { ...c, color: colors[i % colors.length], pct, offset };
    });
  }

  get barMax(): number {
    return Math.max(...(this.dashboard()?.claimsByStatus.map(s => s.count) ?? [1]), 1);
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

  barColor(s: string): string {
    const m: Record<string, string> = {
      FILED: '#853953', SURVEY_ASSIGNED: '#8b5cf6',
      UNDER_REVIEW: '#f59e0b', APPROVED: '#10b981',
      REJECTED: '#ef4444', PAID: '#14b8a6',
    };
    return m[s] ?? '#6b7280';
  }

  formatCurrency(n: number): string {
    return '₹' + n.toLocaleString();
  }

  review(id: number) {
    this.router.navigate(['/claims-officer/claims', id]);
  }
}
