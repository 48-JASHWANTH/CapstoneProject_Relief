import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AgentDashboardService, AgentDashboardResponse } from '../../services/agent-dashboard';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-agent-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './agent-dashboard.html',
  styleUrl: './agent-dashboard.css',
})
export class AgentDashboard implements OnInit {
  private svc = inject(AgentDashboardService);
  private auth = inject(AuthService);
  data = signal<AgentDashboardResponse | null>(null);
  loading = signal(true);
  private agentId = this.auth.getUserId();

  ngOnInit(): void {
    this.svc.getDashboard(this.agentId).subscribe(d => { this.data.set(d); this.loading.set(false); });
  }

  get barChart1(): { label: string; height: number; value: number; color: string }[] {
    if (!this.data()) return [];
    const entries = Object.entries(this.data()!.policiesByDisasterType);
    const max = Math.max(...entries.map(([, v]) => v), 1);
    const colors = ['#853953', '#853953', '#f59e0b', '#10b981', '#ef4444'];
    return entries.map(([label, value], i) => ({ label, value, height: Math.round((value / max) * 120), color: colors[i % colors.length] }));
  }

  get barChart2(): { label: string; height: number; value: number; color: string }[] {
    if (!this.data()) return [];
    const entries = Object.entries(this.data()!.lossFrequencyByDisasterType);
    if (!entries.length) return [];
    const max = Math.max(...entries.map(([, v]) => v), 1);
    const colors = ['#ef4444', '#f59e0b', '#853953', '#8b5cf6'];
    return entries.map(([label, value], i) => ({ label, value, height: Math.round((value / max) * 120), color: colors[i % colors.length] }));
  }

  get donutGradient(): string {
    const ratio = this.data()?.approvalRatio || 0;
    return `conic-gradient(#10b981 0% ${ratio}%, #e5e7eb ${ratio}% 100%)`;
  }

  statusClass(status: string): string {
    const m: Record<string, string> = { APPROVED: 'bg-green-100 text-green-700', ACTIVE: 'bg-[#F3F4F4] text-[#612D53]', PENDING: 'bg-amber-100 text-amber-700', UNDER_REVIEW: 'bg-orange-100 text-orange-700', FORWARDED: 'bg-purple-100 text-purple-700', REJECTED: 'bg-red-100 text-red-700' };
    return m[status] || 'bg-gray-100 text-gray-600';
  }

  claimStatusClass(status: string): string {
    const m: Record<string, string> = { PENDING: 'bg-amber-100 text-amber-700', APPROVED: 'bg-green-100 text-green-700', REJECTED: 'bg-red-100 text-red-700', UNDER_REVIEW: 'bg-[#F3F4F4] text-[#612D53]' };
    return m[status] || 'bg-gray-100 text-gray-600';
  }

  formatCurrency(v: number): string { return '₹' + v.toLocaleString(); }
}
