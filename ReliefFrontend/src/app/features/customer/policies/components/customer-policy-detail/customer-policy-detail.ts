import { Component, Input, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerPolicyService, PolicyResponse } from '../../services/customer-policy';
import { CustomerPaymentService } from '../../../payments/services/customer-payment';
import { PayPremiumDialog } from '../pay-premium-dialog/pay-premium-dialog';
import { AuthService } from '../../../../../core/services/auth';

import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-customer-policy-detail',
  imports: [CommonModule, PayPremiumDialog, FormsModule],
  templateUrl: './customer-policy-detail.html',
  styleUrl: './customer-policy-detail.css',
})
export class CustomerPolicyDetail implements OnInit {
  @Input() id!: string;
  private svc = inject(CustomerPolicyService);
  private paymentSvc = inject(CustomerPaymentService);
  private router = inject(Router);
  private auth = inject(AuthService);
  policy = signal<PolicyResponse | null>(null);
  loading = signal(true);
  uploadingDoc = signal(false);
  successMsg = signal('');
  errorMsg = signal('');
  showConfirmDialog = signal(false);
  validationErrors: { yearBuilt?: string, roofAge?: string, material?: string, safety?: string } = {};
  showPayDialog = signal(false);
  userId = this.auth.getUserId();

  ngOnInit() {
    this.svc.getById(this.userId, Number(this.id)).subscribe(p => {
      this.policy.set(p ?? null);
      this.loading.set(false);
    });
  }

  onPayConfirmed() {
    this.paymentSvc.payPremium(this.userId, this.policy()!.id).subscribe(() => {
      this.policy.update(p => p ? { ...p, status: 'ACTIVE' } : p);
      this.showPayDialog.set(false);
      this.toast('Premium paid successfully! Policy is now ACTIVE');
    });
  }

  // Advanced Details
  yearBuilt: number | null = null;
  roofAge: number | null = null;
  constructionMaterial: string[] = [];
  safetyFeatures: string[] = [];

  availableMaterials = ['Brick', 'Wood', 'Concrete', 'Steel', 'Stone', 'Metal', 'Vinyl', 'Composite', 'Stucco', 'Tile', 'Slate', 'Other'];
  availableSafetyFeatures = ['None', 'Sprinklers', 'Fire Alarms', 'Security System', 'Deadbolts', 'Fire Extinguishers', 'Storm Shutters', 'Backup Generator'];

  // File Upload
  selectedFile: File | null = null;
  documentType: string = 'ID_PROOF';

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        this.notifyError('File size cannot exceed 5MB. Please choose a smaller file.');
        event.target.value = '';
        this.selectedFile = null;
        return;
      }
      const allowedTypes = ['image/jpeg', 'image/png', 'application/pdf'];
      if (!allowedTypes.includes(file.type)) {
        this.notifyError('Only JPG, PNG, and PDF files are allowed.');
        event.target.value = '';
        this.selectedFile = null;
        return;
      }
      this.selectedFile = file;
    }
  }

  notifyError(msg: string) {
    this.errorMsg.set(msg);
    setTimeout(() => this.errorMsg.set(''), 5000);
  }

  toggleMaterial(mat: string) {
    if (this.constructionMaterial.includes(mat)) {
      this.constructionMaterial = this.constructionMaterial.filter(m => m !== mat);
    } else {
      this.constructionMaterial.push(mat);
    }
  }

  toggleSafetyFeature(feat: string) {
    if (this.safetyFeatures.includes(feat)) {
      this.safetyFeatures = this.safetyFeatures.filter(f => f !== feat);
    } else {
      this.safetyFeatures.push(feat);
    }
  }

  viewDocument(fileUrl: string) {
    this.svc.downloadDocument(fileUrl).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: () => this.notifyError('Failed to access document securely.')
    });
  }

  uploadDocument() {
    if (!this.selectedFile) return;
    this.uploadingDoc.set(true);
    this.svc.uploadDocument(this.userId, this.policy()!.id, this.documentType, this.selectedFile).subscribe({
      next: (doc) => {
        const currentPolicy = this.policy();
        if (currentPolicy) {
          const docs = currentPolicy.documents || [];
          this.policy.set({ ...currentPolicy, documents: [...docs, doc] });
        }
        this.selectedFile = null;
        this.documentType = 'ID_PROOF';
        this.uploadingDoc.set(false);
        this.toast('Document uploaded successfully!');
      },
      error: (err) => {
        this.uploadingDoc.set(false);
        this.notifyError(err?.error?.message || 'Failed to upload document.');
      }
    });
  }

  submitAdvancedDetails() {
    this.validationErrors = {};
    let valid = true;

    if (!this.yearBuilt) { this.validationErrors.yearBuilt = "Year built is required"; valid = false; }
    if (this.roofAge === null || this.roofAge === undefined) { this.validationErrors.roofAge = "Roof age is required"; valid = false; }
    if (this.constructionMaterial.length === 0) { this.validationErrors.material = "Please select at least one material"; valid = false; }
    if (this.safetyFeatures.length === 0) { this.validationErrors.safety = "Please select at least one safety feature"; valid = false; }

    if (!valid) return;

    this.showConfirmDialog.set(true);
  }

  confirmSubmitAdvancedDetails() {
    this.showConfirmDialog.set(false);

    const req = {
      yearBuilt: this.yearBuilt || 0,
      roofAge: this.roofAge || 0,
      constructionMaterial: Array.isArray(this.constructionMaterial) ? this.constructionMaterial.join(', ') : this.constructionMaterial,
      previousClaimsHistory: 'None',
      safetyFeatures: Array.isArray(this.safetyFeatures) ? this.safetyFeatures.join(', ') : this.safetyFeatures
    };
    this.svc.submitAdvancedDetails(this.userId, this.policy()!.id, req).subscribe({
      next: (updatedPolicy) => {
        this.policy.set(updatedPolicy);
        this.toast('Details submitted successfully. Policy is now under review!');
      }
    });
  }

  statusClass(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'bg-yellow-100 text-yellow-700',
      APPROVED: 'bg-[#F3F4F4] text-[#612D53]',
      ACTIVE: 'bg-green-100 text-green-700',
      EXPIRED: 'bg-gray-100 text-gray-600',
      REJECTED: 'bg-red-100 text-red-700',
    };
    return m[s] ?? 'bg-gray-100 text-gray-600';
  }

  back() { this.router.navigate(['/customer/policies']); }

  toast(msg: string) {
    this.successMsg.set(msg);
    setTimeout(() => this.successMsg.set(''), 4000);
  }
}

