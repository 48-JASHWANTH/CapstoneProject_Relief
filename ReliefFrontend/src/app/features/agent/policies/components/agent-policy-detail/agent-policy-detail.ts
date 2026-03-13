import { Component, Input, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AgentPolicyService } from '../../services/agent-policy';
import { PolicyResponse } from '../../../../../core/models/policy.model';
import { AdjustPremiumDialog } from '../adjust-premium-dialog/adjust-premium-dialog';
import { AuthService } from '../../../../../core/services/auth';

@Component({
  selector: 'app-agent-policy-detail',
  imports: [CommonModule, AdjustPremiumDialog],
  templateUrl: './agent-policy-detail.html',
  styleUrl: './agent-policy-detail.css',
})
export class AgentPolicyDetail implements OnInit {
  @Input() id!: string;
  private svc = inject(AgentPolicyService);
  private router = inject(Router);
  private auth = inject(AuthService);
  policy = signal<PolicyResponse | null>(null);
  loading = signal(true);
  showAdjustDialog = signal(false);
  successMsg = signal('');
  errorMsg = signal('');
  agentId = this.auth.getUserId();

  ngOnInit(): void {
    this.svc.getById(this.agentId, Number(this.id)).subscribe(p => { this.policy.set(p || null); this.loading.set(false); });
  }

  onAdjustSaved(data: { adjustedSumInsured: number; adjustedPremium: number; remarks: string }): void {
    this.svc.adjustPremium(this.agentId, this.policy()!.id, data).subscribe({
      next: (updated) => { this.policy.set(updated); this.showAdjustDialog.set(false); this.notify('Coverage and premium updated successfully'); },
      error: (err) => { this.notifyError(err?.error?.message || 'Failed to adjust premium. Only PENDING policies can be adjusted.'); }
    });
  }

  notify(msg: string): void { this.successMsg.set(msg); this.errorMsg.set(''); setTimeout(() => this.successMsg.set(''), 3000); }
  notifyError(msg: string): void { this.errorMsg.set(msg); this.successMsg.set(''); setTimeout(() => this.errorMsg.set(''), 4000); }
  statusClass(s: string): string {
    const m: Record<string, string> = { APPROVED: 'bg-green-100 text-green-700', ACTIVE: 'bg-[#F3F4F4] text-[#612D53]', PENDING: 'bg-amber-100 text-amber-700', UNDER_REVIEW: 'bg-orange-100 text-orange-700', FORWARDED: 'bg-purple-100 text-purple-700', REJECTED: 'bg-red-100 text-red-700' };
    return m[s] || 'bg-gray-100 text-gray-600';
  }
  formatCurrency(v: number): string { return '₹' + v.toLocaleString(); }
  back(): void { this.router.navigate(['/agent/policies']); }
}
