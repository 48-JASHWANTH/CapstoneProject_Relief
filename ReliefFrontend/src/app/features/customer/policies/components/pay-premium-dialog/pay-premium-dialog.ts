import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PolicyResponse } from '../../services/customer-policy';

@Component({
  selector: 'app-pay-premium-dialog',
  imports: [CommonModule],
  templateUrl: './pay-premium-dialog.html',
  styleUrl: './pay-premium-dialog.css',
})
export class PayPremiumDialog {
  @Input() policy: PolicyResponse | null = null;
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}

