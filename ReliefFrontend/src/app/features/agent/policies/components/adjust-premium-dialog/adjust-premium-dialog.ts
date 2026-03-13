import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PolicyResponse } from '../../../../../core/models/policy.model';
import { AgentPolicyService } from '../../services/agent-policy';

@Component({
  selector: 'app-adjust-premium-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './adjust-premium-dialog.html',
  styleUrl: './adjust-premium-dialog.css',
})
export class AdjustPremiumDialog implements OnInit {
  @Input() policy: PolicyResponse | null = null;
  @Input() agentId!: number;
  @Input() policyId!: number;
  @Output() save = new EventEmitter<{ adjustedSumInsured: number; adjustedPremium: number; remarks: string }>();
  @Output() cancel = new EventEmitter<void>();

  private svc = inject(AgentPolicyService);

  adjustedSumInsured = 0;
  adjustedPremium = 0;
  remarks = '';
  calculatingPremium = false;

  ngOnInit(): void {
    if (this.policy) {
      this.adjustedSumInsured = this.policy.sumInsured;
      this.recalculatePremium();
    }
  }

  recalculatePremium(): void {
    if (this.adjustedSumInsured <= 0) { this.adjustedPremium = 0; return; }
    this.calculatingPremium = true;
    this.svc.calculatePremium(this.agentId, this.policyId, this.adjustedSumInsured).subscribe({
      next: (premium) => { this.adjustedPremium = premium; this.calculatingPremium = false; },
      error: () => { this.adjustedPremium = 0; this.calculatingPremium = false; },
    });
  }

  get isValid(): boolean {
    if (this.adjustedSumInsured <= 0) return false;
    if (this.adjustedPremium <= 0) return false;
    if (this.adjustedPremium > this.adjustedSumInsured) return false;
    if (this.remarks.trim().length < 5) return false;
    return true;
  }

  get premiumExceedsSumInsured(): boolean {
    return this.adjustedSumInsured > 0 && this.adjustedPremium > this.adjustedSumInsured;
  }

  onSave(): void {
    this.save.emit({
      adjustedSumInsured: this.adjustedSumInsured,
      adjustedPremium: this.adjustedPremium,
      remarks: this.remarks,
    });
  }
  onCancel(): void { this.cancel.emit(); }
}


