import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomerPolicyService } from '../../services/customer-policy';
import { AuthService } from '../../../../../core/services/auth';

function sumInsuredValidator(control: AbstractControl): ValidationErrors | null {
  const group = control.parent;
  if (!group) return null;
  const propertyValue = Number(group.get('propertyValue')?.value ?? 0);
  const sumInsured = Number(control.value ?? 0);
  if (sumInsured > 0 && propertyValue > 0 && sumInsured > propertyValue) {
    return { exceedsPropertyValue: true };
  }
  return null;
}



@Component({
  selector: 'app-customer-new-policy',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-new-policy.html',
  styleUrl: './customer-new-policy.css',
})
export class CustomerNewPolicy implements OnInit {
  private fb = inject(FormBuilder);
  private svc = inject(CustomerPolicyService);
  private router = inject(Router);
  private auth = inject(AuthService);
  form!: FormGroup;
  userId = this.auth.getUserId();
  submitting = signal(false);

  disasterTypes: string[] = [];
  regions: string[] = [];
  tenureOptions: number[] = [];
  policyTypes = [
    { value: 'BASIC',    label: 'BASIC',    hint: 'Economy coverage (1% of sum insured)',        rate: 0.01 },
    { value: 'STANDARD', label: 'STANDARD', hint: 'Balanced coverage (2% of sum insured)',       rate: 0.02 },
    { value: 'PREMIUM',  label: 'PREMIUM',  hint: 'Comprehensive coverage (3% of sum insured)',  rate: 0.03 },
  ];



  ngOnInit() {
    this.svc.getPolicyOptions().subscribe(opts => {
      this.disasterTypes = opts.disasterTypes;
      this.regions = opts.regions;
      this.tenureOptions = opts.tenureOptions;
    });

    this.form = this.fb.group({
      disasterType:    ['', Validators.required],
      region:          ['', Validators.required],
      tenure:          [1, [Validators.required, Validators.min(1)]],
      policyType:      ['STANDARD', Validators.required],
      propertyAddress: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(300)]],
      propertyValue:   [null, [Validators.required, Validators.min(1)]],
      sumInsured:      [null, [Validators.required, Validators.min(1), sumInsuredValidator]],
      premiumAmount:   [null, [Validators.required, Validators.min(1)]],
    });
  }

  onPropertyValueChange() {
    this.form.get('sumInsured')?.updateValueAndValidity();
  }

  formatCurrencyView(controlName: string): string {
    const val = this.form.get(controlName)?.value;
    return val != null && val !== '' ? Number(val).toLocaleString('en-IN') : '';
  }

  onCurrencyInput(controlName: string, event: Event) {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value.replace(/[^0-9]/g, '');
    const num = rawValue ? parseInt(rawValue, 10) : null;
    
    // Set the hidden reactive form state to the true integer
    this.form.get(controlName)?.setValue(num, { emitEvent: true });
    this.form.get(controlName)?.markAsDirty();
    
    // Update the visible input value to show commas (en-IN)
    input.value = num != null ? num.toLocaleString('en-IN') : '';
    
    if (controlName === 'propertyValue') this.onPropertyValueChange();
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.svc.submitPolicy(this.userId, this.form.value).subscribe({
      next: () => this.router.navigate(['/customer/policies']),
      error: () => this.submitting.set(false),
    });
  }

  cancel() { 
    this.router.navigate(['/customer/policies']); 
  }

  field(n: string) { 
    return this.form.get(n)!; 
  }

  invalid(n: string) {
     return this.field(n).invalid && this.field(n).touched; 
  }
}

