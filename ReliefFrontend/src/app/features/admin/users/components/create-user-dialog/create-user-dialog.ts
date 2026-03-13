import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateUserRequest } from '../../services/admin-user';

@Component({
  selector: 'app-create-user-dialog',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-user-dialog.html',
})
export class CreateUserDialog implements OnInit {
  @Output() save = new EventEmitter<CreateUserRequest>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;
  submitting = false;

  readonly roles = ['ADMIN', 'AGENT', 'CUSTOMER', 'CLAIMS_OFFICER'];
  readonly regions = ['NORTH', 'SOUTH', 'EAST', 'WEST', 'CENTRAL'];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      roleName: ['', Validators.required],
      licenseNumber: [''],
      region: [''],
    });
  }

  get isAgent(): boolean {
    return this.form.get('roleName')?.value === 'AGENT';
  }

  selectRole(role: string): void {
    this.form.patchValue({ roleName: role });
    this.updateAgentValidators(role);
  }

  private updateAgentValidators(role: string): void {
    const licControl = this.form.get('licenseNumber')!;
    const regControl = this.form.get('region')!;
    if (role === 'AGENT') {
      licControl.setValidators([Validators.required, Validators.minLength(3)]);
      regControl.setValidators([Validators.required]);
    } else {
      licControl.clearValidators();
      regControl.clearValidators();
    }
    licControl.updateValueAndValidity();
    regControl.updateValueAndValidity();
  }

  isRoleSelected(role: string): boolean {
    return this.form.get('roleName')?.value === role;
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    const val = this.form.value;
    if (!val.roleName) return;
    this.save.emit(val as CreateUserRequest);
  }
}
