import { Component, Input, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AgentPolicyService, AiPremiumDecision } from '../../services/agent-policy';
import { PolicyResponse } from '../../../../../core/models/policy.model';
import { AdjustPremiumDialog } from '../adjust-premium-dialog/adjust-premium-dialog';
import { AuthService } from '../../../../../core/services/auth';

import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-agent-policy-detail',
  imports: [CommonModule, AdjustPremiumDialog, FormsModule],
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
  
  aiDecision = signal<AiPremiumDecision | null>(null);
  loadingAi = signal(false);

  fetchAiEstimate() {
    this.loadingAi.set(true);
    this.svc.getAiPremiumEstimate(this.agentId, this.policy()!.id).subscribe({
      next: (res) => {
        this.aiDecision.set(res);
        this.loadingAi.set(false);
        this.notify('AI Underwriting Assessment Complete');
      },
      error: (err) => {
        this.loadingAi.set(false);
        this.notifyError('AI assessment failed: ' + (err?.error?.message || err.message));
      }
    });
  }

  ngOnInit(): void {
    this.svc.getById(this.agentId, Number(this.id)).subscribe(p => { this.policy.set(p || null); this.loading.set(false); });
  }

  get canAdjustPremium(): boolean {
    const p = this.policy();
    if (!p) return false;
    
    // Check if advanced details are submitted
    if (!p.yearBuilt) return false;
    
    // Check if at least 3 documents are uploaded
    if (!p.documents || p.documents.length < 3) return false;
    
    return true;
  }

  onAdjustSaved(data: { adjustedSumInsured: number; adjustedPremium: number; remarks: string }): void {
    this.svc.adjustPremium(this.agentId, this.policy()!.id, data).subscribe({
      next: (updated) => { this.policy.set(updated); this.showAdjustDialog.set(false); this.notify('Coverage and premium updated successfully'); },
      error: (err) => { this.notifyError(err?.error?.message || 'Failed to adjust premium. Only PENDING/UNDER_REVIEW policies can be adjusted.'); }
    });
  }

  reviewingDoc: number | null = null;
  remarkInput: string = '';

  viewDocument(fileUrl: string) {
    this.svc.downloadDocument(fileUrl).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: () => this.notifyError('Failed to access document securely.')
    });
  }

  reviewDocument(docId: number, status: string) {
    this.reviewingDoc = docId;
    this.svc.reviewDocument(this.agentId, docId, status, this.remarkInput).subscribe({
      next: (updatedDoc) => {
        const currentPolicy = this.policy();
        if (currentPolicy && currentPolicy.documents) {
          const docs = currentPolicy.documents.map(d => d.id === docId ? updatedDoc : d);
          this.policy.set({ ...currentPolicy, documents: docs });
        }
        this.reviewingDoc = null;
        this.remarkInput = '';
        this.notify('Document status updated to ' + status);
      },
      error: () => {
        this.reviewingDoc = null;
        this.notifyError('Failed to review document');
      }
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
