import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminClaimsService, AdminClaimSummary, AssignOfficerRequest } from '../../services/admin-claims';
import { UserResponse } from '../../../../../core/models/user.model';

@Component({
  selector: 'app-admin-claims',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-claims.html',
  styleUrl: './admin-claims.css',
})
export class AdminClaims implements OnInit {
  private svc = inject(AdminClaimsService);
  claims = signal<AdminClaimSummary[]>([]);
  filtered = signal<AdminClaimSummary[]>([]);
  officers = signal<UserResponse[]>([]);
  loading = signal(true);

  filterStatus = 'ALL';
  filterRegion = 'ALL';
  filterAssigned = 'ALL';

  showAssignDialog = signal(false);
  selectedClaim = signal<AdminClaimSummary | null>(null);
  selectedOfficerId: number | null = null;

  page = signal(0);
  pageSize = 10;

  ngOnInit(): void {
    this.svc.getOfficers().subscribe(officers => {
      this.officers.set(officers);
    });
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.svc.getAll().subscribe(data => {
      this.claims.set(data);
      this.applyFilters();
      this.loading.set(false);
    });
  }

  applyFilters(): void {
    let result = [...this.claims()];
    if (this.filterStatus !== 'ALL') result = result.filter(c => c.status === this.filterStatus);
    if (this.filterRegion !== 'ALL') result = result.filter(c => c.region === this.filterRegion);
    if (this.filterAssigned === 'UNASSIGNED') result = result.filter(c => c.assignedOfficerId === null);
    if (this.filterAssigned === 'ASSIGNED') result = result.filter(c => c.assignedOfficerId !== null);
    this.filtered.set(result);
    this.page.set(0);
  }

  get paginated(): AdminClaimSummary[] {
    return this.filtered().slice(this.page() * this.pageSize, (this.page() + 1) * this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filtered().length / this.pageSize);
  }

  get availableOfficersForSelection(): UserResponse[] {
    const claim = this.selectedClaim();
    if (!claim) return [];
    return this.officers().filter(o => !o.region || o.region === claim.region);
  }

  openAssignDialog(claim: AdminClaimSummary): void {
    if (claim.assignedOfficerId) return; // already assigned
    this.selectedClaim.set(claim);
    this.selectedOfficerId = null;
    this.showAssignDialog.set(true);
  }

  confirmAssign(): void {
    if (!this.selectedClaim() || !this.selectedOfficerId) return;
    const req: AssignOfficerRequest = { officerUserId: this.selectedOfficerId };
    this.svc.assignOfficer(this.selectedClaim()!.id, req).subscribe(() => {
      this.showAssignDialog.set(false);
      this.selectedClaim.set(null);
      this.selectedOfficerId = null;
      this.load();
    });
  }

  statusClass(status: string): string {
    const map: Record<string, string> = {
      APPROVED: 'bg-green-100 text-green-700',
      UNDER_REVIEW: 'bg-yellow-100 text-yellow-700',
      PENDING: 'bg-[#F3F4F4] text-[#612D53]',
      REJECTED: 'bg-red-100 text-red-700',
      SETTLED: 'bg-[#F3F4F4] text-[#612D53]',
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  disasterClass(type: string): string {
    const map: Record<string, string> = {
      FLOOD: 'bg-[#F3F4F4] text-[#612D53]',
      EARTHQUAKE: 'bg-[#F3F4F4] text-[#612D53]',
      CYCLONE: 'bg-[#F3F4F4] text-[#612D53]',
      HURRICANE: 'bg-red-100 text-red-700',
      WILDFIRE: 'bg-[#F3F4F4] text-[#612D53]',
      LANDSLIDE: 'bg-stone-100 text-stone-700',
    };
    return map[type] || 'bg-gray-100 text-gray-600';
  }

  formatCurrency(v: number): string {
    return '₹' + v.toLocaleString();
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
