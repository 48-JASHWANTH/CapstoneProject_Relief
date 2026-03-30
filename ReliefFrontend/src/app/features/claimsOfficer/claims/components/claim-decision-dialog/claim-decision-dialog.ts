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

  get collectiveDamageReport() {
    if (!this.claim || !this.claim.documents) return null;
    const photos = this.claim.documents.filter(d => d.aiDamageType && d.aiDamageType !== 'Unknown' && d.aiDamageType !== 'N/A');
    const docs = this.claim.documents.filter(d => !!d.aiSummary);

    if (photos.length === 0 && docs.length === 0) return null;

    let severities = photos.map(p => p.aiSeverity).filter(s => !!s);
    let maxSeverity = 'None';
    if (severities.includes('Severe')) maxSeverity = 'Severe';
    else if (severities.includes('Moderate')) maxSeverity = 'Moderate';
    else if (severities.includes('Minor')) maxSeverity = 'Minor';

    let lossRangesStr = photos.map(p => p.aiSuggestedLoss).filter(l => !!l && l !== 'N/A');
    let totalMin = 0;
    let totalMax = 0;
    for (const str of lossRangesStr) {
      if (!str) continue;
      const cleanStr = String(str).replace(/[^\d-]/g, '').trim();
      const parts = cleanStr.split('-').map(p => parseInt(p.trim() || '0', 10));
      if (parts.length === 2 && !isNaN(parts[0]) && !isNaN(parts[1])) {
        totalMin += parts[0];
        totalMax += parts[1];
      } else if (parts.length > 0 && !isNaN(parts[0])) {
        totalMin += parts[0];
        totalMax += parts[0];
      }
    }

    let formattedRange = 'N/A';
    if (totalMin > 0 || totalMax > 0) {
      if (totalMin === totalMax) {
        formattedRange = `₹${totalMin.toLocaleString()}`;
      } else {
        formattedRange = `₹${totalMin.toLocaleString()} - ₹${totalMax.toLocaleString()}`;
      }
    }

    return {
      maxSeverity,
      lossRanges: formattedRange,
      documentSummaries: docs.map(d => d.aiSummary)
    };
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
