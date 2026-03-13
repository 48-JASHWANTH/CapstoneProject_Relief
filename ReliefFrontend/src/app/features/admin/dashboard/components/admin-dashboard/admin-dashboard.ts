import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminDashboardService, AdminDashboardResponse } from '../../services/admin-dashboard';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  private svc = inject(AdminDashboardService);
  dashboard = signal<AdminDashboardResponse | null>(null);
  loading = signal(true);

  ngOnInit(): void {
    this.svc.getDashboard().subscribe(data => {
      this.dashboard.set(data);
      this.loading.set(false);
    });
  }

  get barChartData(): { label: string; value: number; height: number; pct: number }[] {
    if (!this.dashboard()) return [];
    const entries = Object.entries(this.dashboard()!.policiesByDisasterType);
    const max = Math.max(...entries.map(e => e[1]), 1);
    return entries.map(([label, value]) => ({
      label,
      value,
      height: Math.round((value / max) * 140),
      pct: Math.round((value / max) * 100),
    }));
  }

  get pieData(): { label: string; value: number; color: string; pct: number }[] {
    if (!this.dashboard()) return [];
    const colorMap: Record<string, string> = {
      APPROVED: '#22c55e',
      FILED: '#f59e0b',
      UNDER_REVIEW: '#f97316',
      REJECTED: '#ef4444',
      PAID: '#853953',
      SURVEY_ASSIGNED: '#a855f7',
    };
    const entries = Object.entries(this.dashboard()!.claimsByStatus);
    const total = entries.reduce((s, [, v]) => s + v, 0) || 1;
    return entries.map(([label, value]) => ({
      label,
      value,
      color: colorMap[label] || '#6b7280',
      pct: Math.round((value / total) * 100),
    }));
  }

  get pieGradient(): string {
    const slices = this.pieData;
    if (!slices.length) return 'conic-gradient(#e5e7eb 0% 100%)';
    let start = 0;
    const parts = slices.map(s => {
      const end = start + s.pct;
      const part = `${s.color} ${start}% ${end}%`;
      start = end;
      return part;
    });
    return `conic-gradient(${parts.join(', ')})`;
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      HEALTHY: 'bg-green-100 text-green-700',
      WARNING: 'bg-amber-100 text-amber-700',
      CRITICAL: 'bg-red-100 text-red-700',
    };
    return map[status] || 'bg-gray-100 text-gray-700';
  }

  formatCurrency(v: number): string {
    return '₹' + v.toLocaleString();
  }
}
