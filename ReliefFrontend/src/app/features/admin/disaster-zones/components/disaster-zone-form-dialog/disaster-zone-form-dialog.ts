import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DisasterZoneResponse } from '../../../../../core/models/disaster-zone.model';
import { AdminRiskPoolService } from '../../../risk-pools/services/admin-risk-pool';

@Component({
  selector: 'app-disaster-zone-form-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './disaster-zone-form-dialog.html',
  styleUrl: './disaster-zone-form-dialog.css',
})
export class DisasterZoneFormDialog implements OnInit {
  @Input() zone: DisasterZoneResponse | null = null;
  @Input() isEdit = false;
  @Output() save = new EventEmitter<{ zoneName: string; location: string; riskLevel: string; disasterType: string }>();
  @Output() cancel = new EventEmitter<void>();

  private riskPoolSvc = inject(AdminRiskPoolService);

  zoneName = '';
  location = 'NORTH';
  riskLevel = 'MEDIUM';
  disasterType = '';
  locations = ['NORTH', 'SOUTH', 'EAST', 'WEST', 'CENTRAL'];
  riskLevels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  disasterTypes: string[] = [];

  ngOnInit(): void {
    if (this.zone) {
      this.zoneName = this.zone.zoneName;
      this.location = this.zone.location;
      this.riskLevel = this.zone.riskLevel;
      this.disasterType = this.zone.disasterType;
    }
    this.riskPoolSvc.getDisasterTypes().subscribe(types => {
      const allTypes = [...new Set([...types, ...(this.disasterType ? [this.disasterType] : [])])].filter(t => !!t);
      this.disasterTypes = allTypes.length > 0 ? allTypes : ['FLOOD', 'EARTHQUAKE', 'CYCLONE', 'HURRICANE'];
      if (!this.disasterType && this.disasterTypes.length > 0) {
        this.disasterType = this.disasterTypes[0];
      }
    });
  }

  onSave(): void {
    this.save.emit({ zoneName: this.zoneName, location: this.location, riskLevel: this.riskLevel, disasterType: this.disasterType });
  }

  onCancel(): void { this.cancel.emit(); }
}
