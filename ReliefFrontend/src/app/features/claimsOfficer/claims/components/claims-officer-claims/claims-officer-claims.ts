import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClaimsOfficerClaimsService, ClaimResponse, ClaimDecisionRequest } from '../../services/claims-officer-claims';
import { ClaimDecisionDialog } from '../claim-decision-dialog/claim-decision-dialog';

@Component({
  selector: 'app-claims-officer-claims',
  imports: [CommonModule, FormsModule, ClaimDecisionDialog],
  templateUrl: './claims-officer-claims.html',
  styleUrl: './claims-officer-claims.css',
})
export class ClaimsOfficerClaims implements OnInit {
  private svc = inject(ClaimsOfficerClaimsService);
  private router = inject(Router);
  all = signal<ClaimResponse[]>([]);
  filtered = signal<ClaimResponse[]>([]);
  loading = signal(true);
  successMsg = signal('');

  filterStatus = 'ALL';
  filterDisaster = 'ALL';
  statuses = ['ALL','FILED','SURVEY_ASSIGNED','UNDER_REVIEW','APPROVED','REJECTED','PAID'];
  disasters = ['ALL','FLOOD','EARTHQUAKE','WILDFIRE','HURRICANE','LANDSLIDE'];

  showDecisionDialog = signal(false);
  selectedClaim = signal<ClaimResponse | null>(null);

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.svc.getAll().subscribe(c => {
      this.all.set(c);
      this.applyFilters();
      this.loading.set(false);
    });
  }

  applyFilters() {
    this.filtered.set(this.all()
      .filter(c => this.filterStatus === 'ALL' || c.status === this.filterStatus)
      .filter(c => this.filterDisaster === 'ALL' || c.disasterType === this.filterDisaster));
  }

  canMarkUnderReview(c: ClaimResponse) { return c.status === 'FILED' || c.status === 'SURVEY_ASSIGNED'; }
  canDecide(c: ClaimResponse) { return c.status === 'UNDER_REVIEW'; }

  markUnderReview(c: ClaimResponse) {
    this.svc.markUnderReview(c.id).subscribe(() => {
      this.load();
      this.toast('Claim marked Under Review');
    });
  }

  openDecision(c: ClaimResponse) {
    this.selectedClaim.set(c);
    this.showDecisionDialog.set(true);
  }

  onDecisionSaved(req: ClaimDecisionRequest) {
    this.svc.decideClaim(this.selectedClaim()!.id, req).subscribe(() => {
      this.showDecisionDialog.set(false);
      this.load();
      this.toast('Claim decision saved');
    });
  }

  viewDetail(c: ClaimResponse) { this.router.navigate(['/claims-officer/claims', c.id]); }

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
