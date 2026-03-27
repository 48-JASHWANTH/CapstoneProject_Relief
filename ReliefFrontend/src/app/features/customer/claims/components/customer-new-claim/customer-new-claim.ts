import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors, FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerClaimsService, CustomerClaimResponse } from '../../services/customer-claims';
import { CustomerPolicyService, PolicyResponse } from '../../../policies/services/customer-policy';
import { AuthService } from '../../../../../core/services/auth';
import { RequestStateService } from '../../../../../core/services/request-state.service';

@Component({
  selector: 'app-customer-new-claim',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, FormsModule],
  templateUrl: './customer-new-claim.html',
  styleUrl: './customer-new-claim.css',
})
export class CustomerNewClaim implements OnInit {
  private fb = inject(FormBuilder);
  private claimsSvc = inject(CustomerClaimsService);
  private policySvc = inject(CustomerPolicyService);
  private router = inject(Router);
  private auth = inject(AuthService);
  requestState = inject(RequestStateService);
  form!: FormGroup;
  eligiblePolicies = signal<PolicyResponse[]>([]);
  policiesLoaded = signal(false);
  userId = this.auth.getUserId();

  step = signal<number>(1);
  createdClaim = signal<CustomerClaimResponse | null>(null);
  selectedFile: File | null = null;
  documentType: string = 'DAMAGE_PHOTO';
  uploadingDoc = signal(false);

  get selectedPolicy(): PolicyResponse | undefined {
    const policyId = this.form?.get('policyId')?.value;
    return this.eligiblePolicies().find(p => p.id === Number(policyId));
  }

  ngOnInit() {
    this.policySvc.getMyPolicies(this.userId).subscribe(policies => {
      this.eligiblePolicies.set(policies.filter(p => p.status === 'ACTIVE'));
      this.policiesLoaded.set(true);
    });

    this.form = this.fb.group({
      policyId: [null, Validators.required],
      incidentDate: ['', Validators.required],
      damageType: ['', Validators.required],
      description: ['', [Validators.required, Validators.minLength(20)]],
      estimatedLoss: [null, [Validators.required, Validators.min(1), this.maxLossValidator.bind(this)]],
    });
  }

  maxLossValidator(control: AbstractControl): ValidationErrors | null {
    const policy = this.selectedPolicy;
    if (!policy || !control.value) return null;
    return Number(control.value) > policy.sumInsured ? { exceedsSumInsured: true } : null;
  }

  onPolicyChange() {
    this.requestState.error.set('');
    this.form.get('estimatedLoss')?.updateValueAndValidity();
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.requestState.reset();
    const { policyId, incidentDate, damageType, description, estimatedLoss } = this.form.value;
    this.claimsSvc.fileClaim(this.userId, { 
      policyId: Number(policyId), 
      incidentDate,
      damageType,
      description, 
      estimatedLoss: Number(estimatedLoss) 
    }).subscribe({
      next: claim => {
        this.createdClaim.set(claim);
        this.step.set(2);
      },
      error: err => {
        this.requestState.error.set(err.error?.message ?? 'Submission failed. Please try again.');
      }
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        this.requestState.error.set('File size cannot exceed 5MB. Please choose a smaller file.');
        event.target.value = '';
        this.selectedFile = null;
        return;
      }
      const allowedTypes = ['image/jpeg', 'image/png', 'application/pdf'];
      if (!allowedTypes.includes(file.type)) {
        this.requestState.error.set('Only JPG, PNG, and PDF files are allowed.');
        event.target.value = '';
        this.selectedFile = null;
        return;
      }
      this.selectedFile = file;
    }
  }

  uploadDocument() {
    if (!this.selectedFile || !this.createdClaim()) return;
    this.uploadingDoc.set(true);
    this.requestState.error.set('');
    this.claimsSvc.uploadDocument(this.userId, this.createdClaim()!.id, this.documentType, this.selectedFile).subscribe({
      next: (doc) => {
        const currentClaim = this.createdClaim();
        if (currentClaim) {
          const docs = currentClaim.documents || [];
          this.createdClaim.set({ ...currentClaim, documents: [...docs, doc] });
        }
        this.selectedFile = null;
        this.documentType = 'DAMAGE_PHOTO';
        this.uploadingDoc.set(false);
      },
      error: (err) => {
        this.uploadingDoc.set(false);
        this.requestState.error.set(err?.error?.message || 'Failed to upload document.');
      }
    });
  }

  viewDocument(fileUrl: string) {
    this.claimsSvc.downloadDocument(fileUrl).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: () => this.requestState.error.set('Failed to access document securely.')
    });
  }

  finish() {
    localStorage.setItem('relief_claim_success', `Claim ${this.createdClaim()?.claimNumber} filed successfully and is now pending review.`);
    this.router.navigate(['/customer/claims']);
  }

  cancel() { this.router.navigate(['/customer/claims']); }
  clearError() { this.requestState.error.set(''); }

  field(n: string) { return this.form.get(n)!; }
  invalid(n: string) { return this.field(n).invalid && this.field(n).touched; }
}

