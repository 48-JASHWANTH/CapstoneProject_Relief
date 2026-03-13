import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RiskPoolResponse } from '../../../../../core/models/risk-pool.model';

@Component({
  selector: 'app-risk-pool-form-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './risk-pool-form-dialog.html',
  styleUrl: './risk-pool-form-dialog.css',
})
export class RiskPoolFormDialog implements OnInit {
  @Input() pool: RiskPoolResponse | null = null;
  @Input() isEdit = false;
  @Output() save = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  disasterType = '';
  totalPremiumCollected = 0;
  totalClaimsPaid = 0;
  thresholdPercentage = 75;
  poolStatus = 'HEALTHY';
  statuses = ['HEALTHY', 'WARNING', 'CRITICAL'];

  ngOnInit(): void {
    if (this.pool) {
      this.disasterType = this.pool.disasterType;
      this.totalPremiumCollected = this.pool.totalPremiumCollected;
      this.totalClaimsPaid = this.pool.totalClaimsPaid;
      this.thresholdPercentage = this.pool.thresholdPercentage;
      this.poolStatus = this.pool.poolStatus;
    }
  }

  get isValid(): boolean {
    if (!this.isEdit && !this.disasterType.trim()) return false;
    if (this.totalPremiumCollected < 0 || this.totalClaimsPaid < 0) return false;
    return true;
  }

  onSave(): void {
    this.save.emit({ disasterType: this.disasterType, totalPremiumCollected: this.totalPremiumCollected, totalClaimsPaid: this.totalClaimsPaid, thresholdPercentage: this.thresholdPercentage, poolStatus: this.poolStatus });
  }

  onCancel(): void { this.cancel.emit(); }
}
