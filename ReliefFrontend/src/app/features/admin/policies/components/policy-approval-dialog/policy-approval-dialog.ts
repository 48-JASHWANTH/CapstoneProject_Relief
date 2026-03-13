import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PolicyResponse } from '../../../../../core/models/policy.model';

@Component({
  selector: 'app-policy-approval-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './policy-approval-dialog.html',
  styleUrl: './policy-approval-dialog.css',
})
export class PolicyApprovalDialog {
  @Input() policy!: PolicyResponse;
  @Output() save = new EventEmitter<{ status: string; remarks: string }>();
  @Output() cancel = new EventEmitter<void>();

  decision = 'APPROVED';

  onSave(): void { this.save.emit({ status: this.decision, remarks: '' }); }
  onCancel(): void { this.cancel.emit(); }
}
