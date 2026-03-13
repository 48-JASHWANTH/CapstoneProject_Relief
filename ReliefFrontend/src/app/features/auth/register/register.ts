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
