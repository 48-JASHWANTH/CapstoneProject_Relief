import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerClaimsService } from '../../services/customer-claims';
import { CustomerPolicyService, PolicyResponse } from '../../../policies/services/customer-policy';
import { AuthService } from '../../../../../core/services/auth';
import { RequestStateService } from '../../../../../core/services/request-state.service';

@Component({
  selector: 'app-customer-new-claim',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
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
    const { policyId, description, estimatedLoss } = this.form.value;
    this.claimsSvc.fileClaim(this.userId, { policyId: Number(policyId), description, estimatedLoss: Number(estimatedLoss) }).subscribe({
      next: claim => {
        localStorage.setItem('relief_claim_success', `Claim ${claim.claimNumber} filed successfully and is now pending review.`);
        this.router.navigate(['/customer/claims']);
      },
      error: err => {
        this.requestState.error.set(err.error?.message ?? 'Submission failed. Please try again.');
      }
    });
  }

  cancel() { this.router.navigate(['/customer/claims']); }
  clearError() { this.requestState.error.set(''); }

  field(n: string) { return this.form.get(n)!; }
  invalid(n: string) { return this.field(n).invalid && this.field(n).touched; }
}

