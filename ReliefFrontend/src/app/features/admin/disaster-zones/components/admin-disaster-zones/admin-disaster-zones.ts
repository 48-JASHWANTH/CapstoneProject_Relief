import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminDisasterZoneService } from '../../services/admin-disaster-zone';
import { AdminRiskPoolService } from '../../../risk-pools/services/admin-risk-pool';
import { DisasterZoneResponse } from '../../../../../core/models/disaster-zone.model';
import { DisasterZoneFormDialog } from '../disaster-zone-form-dialog/disaster-zone-form-dialog';

@Component({
  selector: 'app-admin-disaster-zones',
  imports: [CommonModule, FormsModule, DisasterZoneFormDialog],
  templateUrl: './admin-disaster-zones.html',
  styleUrl: './admin-disaster-zones.css',
})
export class AdminDisasterZones implements OnInit {
  private svc = inject(AdminDisasterZoneService);
  private riskPoolSvc = inject(AdminRiskPoolService);
  zones = signal<DisasterZoneResponse[]>([]);
  filtered = signal<DisasterZoneResponse[]>([]);
  disasterTypes: string[] = [];
  loading = signal(true);
  filterRisk = 'ALL';
  filterDisaster = 'ALL';
  showFormDialog = signal(false);
  showConfirmDelete = signal(false);
  selectedZone = signal<DisasterZoneResponse | null>(null);
  isEdit = signal(false);
  page = signal(0);
  pageSize = 10;

  ngOnInit(): void { 
    this.load(); 
    this.riskPoolSvc.getDisasterTypes().subscribe(types => {
      this.disasterTypes = types.length > 0 ? types : ['FLOOD', 'EARTHQUAKE', 'CYCLONE', 'HURRICANE'];
    });
  }

  load(): void {
    this.loading.set(true);
    this.svc.getAll().subscribe(data => { this.zones.set(data); this.applyFilters(); this.loading.set(false); });
  }

  applyFilters(): void {
    let r = [...this.zones()];
    if (this.filterRisk !== 'ALL') r = r.filter(z => z.riskLevel === this.filterRisk);
    if (this.filterDisaster !== 'ALL') r = r.filter(z => z.disasterType === this.filterDisaster);
    this.filtered.set(r);
    this.page.set(0);
  }

  get paginated(): DisasterZoneResponse[] {
    return this.filtered().slice(this.page() * this.pageSize, (this.page() + 1) * this.pageSize);
  }

  get totalPages(): number { return Math.ceil(this.filtered().length / this.pageSize); }

  openCreate(): void { this.selectedZone.set(null); this.isEdit.set(false); this.showFormDialog.set(true); }
  openEdit(zone: DisasterZoneResponse): void { this.selectedZone.set(zone); this.isEdit.set(true); this.showFormDialog.set(true); }
  openDelete(zone: DisasterZoneResponse): void { this.selectedZone.set(zone); this.showConfirmDelete.set(true); }

  onFormSaved(data: { zoneName: string; location: string; riskLevel: string; disasterType: string }): void {
    const obs = this.isEdit() ? this.svc.update(this.selectedZone()!.id, data) : this.svc.create(data);
    obs.subscribe(() => { this.showFormDialog.set(false); this.load(); });
  }

  confirmDelete(): void {
    this.svc.delete(this.selectedZone()!.id).subscribe(() => { this.showConfirmDelete.set(false); this.load(); });
  }

  riskClass(level: string): string {
    const map: Record<string, string> = { HIGH: 'bg-red-100 text-red-700', MEDIUM: 'bg-orange-100 text-orange-700', LOW: 'bg-green-100 text-green-700', CRITICAL: 'bg-red-200 text-red-800' };
    return map[level] || 'bg-gray-100 text-gray-600';
  }
}
