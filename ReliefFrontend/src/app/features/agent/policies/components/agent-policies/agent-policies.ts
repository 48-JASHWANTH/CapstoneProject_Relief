import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AgentPolicyService } from '../../services/agent-policy';
import { PolicyResponse } from '../../../../../core/models/policy.model';
import { AdjustPremiumDialog } from '../adjust-premium-dialog/adjust-premium-dialog';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-agent-policies',
  imports: [CommonModule, FormsModule, AdjustPremiumDialog],
  templateUrl: './agent-policies.html',
  styleUrl: './agent-policies.css',
})
export class AgentPolicies implements OnInit {
  private svc = inject(AgentPolicyService);
  private router = inject(Router);
  private auth = inject(AuthService);
  policies = signal<PolicyResponse[]>([]);
  filtered = signal<PolicyResponse[]>([]);
  loading = signal(true);
  searchText = '';
  filterStatus = 'ALL';
  showAdjustDialog = signal(false);
  selectedPolicy = signal<PolicyResponse | null>(null);
  page = signal(0);
  pageSize = 10;
  successMsg = signal('');
  errorMsg = signal('');
  statuses = ['ALL', 'PENDING', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'ACTIVE'];
  agentId = this.auth.getUserId();

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.svc.getMyPolicies(this.agentId).subscribe(data => { this.policies.set(data); this.applyFilters(); this.loading.set(false); });
  }

  applyFilters(): void {
    let r = [...this.policies()];
    if (this.filterStatus !== 'ALL') r = r.filter(p => p.status === this.filterStatus);
    if (this.searchText.trim()) {
      const s = this.searchText.toLowerCase();
      r = r.filter(p => p.policyNumber.toLowerCase().includes(s) || p.userName.toLowerCase().includes(s));
    }
    this.filtered.set(r);
    this.page.set(0);
  }

  get paginated(): PolicyResponse[] { return this.filtered().slice(this.page() * this.pageSize, (this.page() + 1) * this.pageSize); }
  get totalPages(): number { return Math.ceil(this.filtered().length / this.pageSize); }
  canForward(_p: PolicyResponse): boolean { return false; }
  openAdjust(p: PolicyResponse): void { this.selectedPolicy.set(p); this.showAdjustDialog.set(true); }
  viewDetail(p: PolicyResponse): void { this.router.navigate(['/agent/policies', p.id]); }

  onAdjustSaved(data: { adjustedSumInsured: number; adjustedPremium: number; remarks: string }): void {
    this.svc.adjustPremium(this.agentId, this.selectedPolicy()!.id, data).subscribe({
      next: () => { this.showAdjustDialog.set(false); this.notify('Coverage and premium updated successfully'); this.load(); },
      error: (err) => { this.notifyError(err?.error?.message || 'Failed to adjust premium. Only PENDING policies can be adjusted.'); }
    });
  }

  notify(msg: string): void { this.successMsg.set(msg); this.errorMsg.set(''); setTimeout(() => this.successMsg.set(''), 3000); }
  notifyError(msg: string): void { this.errorMsg.set(msg); this.successMsg.set(''); setTimeout(() => this.errorMsg.set(''), 4000); }

  statusClass(s: string): string {
    const m: Record<string, string> = { APPROVED: 'bg-green-100 text-green-700', ACTIVE: 'bg-[#F3F4F4] text-[#612D53]', PENDING: 'bg-amber-100 text-amber-700', UNDER_REVIEW: 'bg-orange-100 text-orange-700', FORWARDED: 'bg-purple-100 text-purple-700', REJECTED: 'bg-red-100 text-red-700' };
    return m[s] || 'bg-gray-100 text-gray-600';
  }

  disasterClass(t: string): string {
    const m: Record<string, string> = { FLOOD: 'bg-[#F3F4F4] text-[#612D53]', EARTHQUAKE: 'bg-orange-100 text-orange-800', CYCLONE: 'bg-teal-100 text-teal-700', HURRICANE: 'bg-purple-100 text-purple-700' };
    return m[t] || 'bg-gray-100 text-gray-600';
  }

  formatCurrency(v: number): string { return '₹' + v.toLocaleString(); }
}
