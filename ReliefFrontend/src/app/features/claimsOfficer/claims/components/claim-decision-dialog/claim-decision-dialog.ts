import { Component, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClaimResponse, ClaimDecisionRequest } from '../../services/claims-officer-claims';

@Component({
  selector: 'app-claim-decision-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './claim-decision-dialog.html',
  styleUrl: './claim-decision-dialog.css',
})
export class ClaimDecisionDialog implements OnChanges {
  @Input() claim: ClaimResponse | null = null;
  @Output() save = new EventEmitter<ClaimDecisionRequest>();
  @Output() cancel = new EventEmitter<void>();

  decision = 'APPROVED';
  approvedAmount = 0;
  remarks = '';

  ngOnChanges() {
    if (this.claim) {
      this.decision = 'APPROVED';
      this.approvedAmount = this.claim.estimatedLoss;
      this.remarks = '';
    }
  }

  get isValid(): boolean {
    if (!this.remarks.trim()) return false;
    if (this.decision === 'APPROVED') {
      if (this.approvedAmount <= 0) return false;
      if (this.claim?.sumInsured != null && this.approvedAmount > this.claim.sumInsured) return false;
    }
    return true;
  }

  get approvedExceedsSumInsured(): boolean {
    return this.decision === 'APPROVED'
      && this.approvedAmount > 0
      && this.claim?.sumInsured != null
      && this.approvedAmount > (this.claim.sumInsured ?? Infinity);
  }

  get sumInsuredDisplay(): string {
    return this.claim?.sumInsured != null
      ? '₹' + this.claim.sumInsured.toLocaleString()
      : 'N/A';
  }

  onSave() {
    this.save.emit({ decision: this.decision, approvedAmount: this.approvedAmount, remarks: this.remarks });
  }

  onCancel() { this.cancel.emit(); }
}
