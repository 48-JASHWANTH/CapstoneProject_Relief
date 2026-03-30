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

  // New specific upload slots
  photo1File: File | null = null;
  photo2File: File | null = null;
  docFile: File | null = null;
  
  uploadStates = signal({
    photo1: 'pending' as 'pending' | 'uploading' | 'done' | 'error',
    photo2: 'pending' as 'pending' | 'uploading' | 'done' | 'error',
    doc: 'pending' as 'pending' | 'uploading' | 'done' | 'error'
  });

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

  onFileSelected(event: any, slot: 'photo1' | 'photo2' | 'doc') {
    const file = event.target.files[0];
    if (!file) return;

    if (file.size > 5 * 1024 * 1024) {
      this.requestState.error.set('File size cannot exceed 5MB.');
      event.target.value = '';
      return;
    }

    const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
    const isPdf = file.type === 'application/pdf';

    if (slot === 'photo1' || slot === 'photo2') {
      if (!isImage) {
        this.requestState.error.set('Photos must be JPG or PNG format.');
        event.target.value = '';
        return;
      }
      if (slot === 'photo1') this.photo1File = file;
      if (slot === 'photo2') this.photo2File = file;
    } else if (slot === 'doc') {
      if (!isPdf) {
        this.requestState.error.set('Supporting Document must be a PDF file.');
        event.target.value = '';
        return;
      }
      this.docFile = file;
    }
  }

  uploadSpecificFile(slot: 'photo1' | 'photo2' | 'doc') {
    let fileToUpload: File | null = null;
    let explicitDocType = 'DAMAGE_PHOTO';
    
    if (slot === 'photo1') fileToUpload = this.photo1File;
    if (slot === 'photo2') fileToUpload = this.photo2File;
    if (slot === 'doc') {
      fileToUpload = this.docFile;
      explicitDocType = 'REPAIR_ESTIMATE';
    }

    if (!fileToUpload || !this.createdClaim()) return;

    this.uploadStates.update(s => ({ ...s, [slot]: 'uploading' }));
    this.requestState.error.set('');
    
    this.claimsSvc.uploadDocument(this.userId, this.createdClaim()!.id, explicitDocType, fileToUpload).subscribe({
      next: (doc) => {
        const currentClaim = this.createdClaim();
        if (currentClaim) {
           this.createdClaim.set({ ...currentClaim, documents: [...(currentClaim.documents || []), doc] });
        }
        this.uploadStates.update(s => ({ ...s, [slot]: 'done' }));
        if (slot === 'photo1') this.photo1File = null;
        if (slot === 'photo2') this.photo2File = null;
        if (slot === 'doc') this.docFile = null;
      },
      error: (err) => {
        this.uploadStates.update(s => ({ ...s, [slot]: 'error' }));
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

  get canFinish(): boolean {
    return this.uploadStates().photo1 === 'done' && this.uploadStates().doc === 'done';
  }

  finish() {
    if (!this.canFinish) return;
    localStorage.setItem('relief_claim_success', `Claim ${this.createdClaim()?.claimNumber} filed successfully and is now pending review.`);
    this.router.navigate(['/customer/claims']);
  }

  cancel() { this.router.navigate(['/customer/claims']); }
  clearError() { this.requestState.error.set(''); }

  field(n: string) { return this.form.get(n)!; }
  invalid(n: string) { return this.field(n).invalid && this.field(n).touched; }
}

