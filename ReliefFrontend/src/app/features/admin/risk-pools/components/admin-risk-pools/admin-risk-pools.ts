import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminRiskPoolService } from '../../services/admin-risk-pool';
import { RiskPoolResponse } from '../../../../../core/models/risk-pool.model';
import { RiskPoolFormDialog } from '../risk-pool-form-dialog/risk-pool-form-dialog';

@Component({
  selector: 'app-admin-risk-pools',
  imports: [CommonModule, FormsModule, RiskPoolFormDialog],
  templateUrl: './admin-risk-pools.html',
  styleUrl: './admin-risk-pools.css',
})
export class AdminRiskPools implements OnInit {
  private svc = inject(AdminRiskPoolService);
  pools = signal<RiskPoolResponse[]>([]);
  loading = signal(true);
  showFormDialog = signal(false);
  showConfirmDelete = signal(false);
  selectedPool = signal<RiskPoolResponse | null>(null);
  isEdit = signal(false);

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.svc.getAll().subscribe(data => { this.pools.set(data); this.loading.set(false); });
  }

  openEdit(pool: RiskPoolResponse): void { this.selectedPool.set(pool); this.isEdit.set(true); this.showFormDialog.set(true); }
  openDelete(pool: RiskPoolResponse): void { this.selectedPool.set(pool); this.showConfirmDelete.set(true); }

  onFormSaved(data: any): void {
    this.svc.update(this.selectedPool()!.id, data).subscribe(() => { this.showFormDialog.set(false); this.load(); });
  }

  confirmDelete(): void {
    this.svc.delete(this.selectedPool()!.id).subscribe(() => { this.showConfirmDelete.set(false); this.load(); });
  }

  statusClass(status: string): string {
    const map: Record<string, string> = { HEALTHY: 'bg-green-100 text-green-700', WARNING: 'bg-amber-100 text-amber-700', CRITICAL: 'bg-red-100 text-red-700' };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  get totalPremium(): number { return this.pools().reduce((s, p) => s + p.totalPremiumCollected, 0); }
  get totalClaimsPaid(): number { return this.pools().reduce((s, p) => s + p.totalClaimsPaid, 0); }
  get criticalCount(): number { return this.pools().filter(p => p.criticalFlag).length; }
  get Math() { return Math; }
  formatCurrency(v: number): string { return '₹' + v.toLocaleString(); }
}
