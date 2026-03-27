import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { RequestStateService } from '../../../core/services/request-state.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private authService = inject(AuthService);
  private router = inject(Router);
  private requestState = inject(RequestStateService);

  fullName = '';
  email = '';
  password = '';
  confirmPassword = '';
  showPassword = signal(false);
  showConfirmPassword = signal(false);

  get hasMinLength(): boolean { return this.password.length >= 8; }
  get hasUppercase(): boolean { return /[A-Z]/.test(this.password); }
  get hasNumber(): boolean { return /[0-9]/.test(this.password); }
  get hasSymbol(): boolean { return /[^a-zA-Z0-9]/.test(this.password); }

  get passwordStrength(): number {
    let score = 0;
    if (!this.password) return 0;
    if (this.hasMinLength) score += 1;
    if (this.hasUppercase) score += 1;
    if (this.hasNumber) score += 1;
    if (this.hasSymbol) score += 1;
    return score;
  }

  get strengthColor(): string {
    const score = this.passwordStrength;
    if (score === 0) return 'bg-gray-200';
    if (score === 1) return 'bg-red-500';
    if (score === 2) return 'bg-orange-500';
    if (score === 3) return 'bg-yellow-500';
    return 'bg-green-500';
  }

  get strengthText(): string {
    const score = this.passwordStrength;
    if (score === 0) return '';
    if (score === 1) return 'Weak';
    if (score === 2) return 'Fair';
    if (score === 3) return 'Good';
    return 'Strong';
  }

  get error() { return this.requestState.error; }
  get loading() { return this.requestState.loading; }
  get success() { return this.requestState.success; }

  register(form: NgForm) {
    // Mark all fields as touched so validation messages show
    form.control.markAllAsTouched();

    if (form.invalid) {
      this.requestState.error.set('Please fill the required fields');
      return;
    }
    if (this.passwordStrength < 4) {
      this.requestState.error.set('Please ensure your password meets all strength requirements.');
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.requestState.error.set('Passwords do not match.');
      return;
    }
    this.requestState.reset();

    this.authService.register({
      name: this.fullName.trim(),
      email: this.email.trim(),
      password: this.password,
    }).subscribe({
      next: () => {
        this.requestState.success.set('done');
        form.reset();
        setTimeout(() => this.router.navigate(['/customer-login']), 1500);
      },
      error: (err) => {
        this.requestState.error.set(err.error?.message || 'Registration failed. Email may already be in use.');
      }
    });
  }
}
