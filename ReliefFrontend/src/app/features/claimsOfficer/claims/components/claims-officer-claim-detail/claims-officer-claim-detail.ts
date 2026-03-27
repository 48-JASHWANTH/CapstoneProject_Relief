import { Component, Input, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClaimsOfficerClaimsService, ClaimResponse, ClaimDecisionRequest } from '../../services/claims-officer-claims';
import { ClaimDecisionDialog } from '../claim-decision-dialog/claim-decision-dialog';
import { RequestStateService } from '../../../../../core/services/request-state.service';

@Component({
  selector: 'app-claims-officer-claim-detail',
  imports: [CommonModule, ClaimDecisionDialog],
  templateUrl: './claims-officer-claim-detail.html',
  styleUrl: './claims-officer-claim-detail.css',
})
export class ClaimsOfficerClaimDetail implements OnInit {
  @Input() id!: string;
  private svc = inject(ClaimsOfficerClaimsService);
  private router = inject(Router);
  requestState = inject(RequestStateService);
  claim = signal<ClaimResponse | null>(null);
  successMsg = signal('');
  showDecisionDialog = signal(false);

  ngOnInit() {
    this.svc.getById(Number(this.id)).subscribe(c => {
      this.claim.set(c ?? null);
    });
  }

  get canMarkUnderReview() { return this.claim()?.status === 'FILED' || this.claim()?.status === 'SURVEY_ASSIGNED'; }
  get canDecide() { return this.claim()?.status === 'UNDER_REVIEW'; }

  markUnderReview() {
    this.svc.markUnderReview(this.claim()!.id).subscribe(c => {
      this.claim.set(c);
      this.toast('Claim marked Under Review');
    });
  }

  viewDocument(fileUrl: string) {
    this.svc.downloadDocument(fileUrl).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: () => this.requestState.error.set('Failed to access document securely.')
    });
  }

  onDecisionSaved(req: ClaimDecisionRequest) {
    this.svc.decideClaim(this.claim()!.id, req).subscribe(c => {
      this.claim.set(c);
      this.showDecisionDialog.set(false);
      this.toast('Claim decision saved');
    });
  }

  back() { this.router.navigate(['/claims-officer/claims']); }

  toast(msg: string) {
    this.successMsg.set(msg);
    setTimeout(() => this.successMsg.set(''), 3000);
  }

  statusClass(s: string): string {
    const m: Record<string, string> = {
      FILED: 'bg-[#F3F4F4] text-[#612D53]', SURVEY_ASSIGNED: 'bg-[#F3F4F4] text-[#612D53]',
      UNDER_REVIEW: 'bg-[#F3F4F4] text-[#612D53]', APPROVED: 'bg-green-100 text-green-700',
      REJECTED: 'bg-red-100 text-red-700', PAID: 'bg-[#F3F4F4] text-[#612D53]',
    };
    return m[s] ?? 'bg-gray-100 text-gray-600';
  }

  formatCurrency(n: number): string { return '₹' + n.toLocaleString(); }
}
