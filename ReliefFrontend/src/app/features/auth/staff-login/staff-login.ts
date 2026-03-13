import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { RequestStateService } from '../../../core/services/request-state.service';

@Component({
  selector: 'app-staff-login',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './staff-login.html',
  styleUrl: './staff-login.css',
})
export class StaffLogin {
  private authService = inject(AuthService);
  private router = inject(Router);
  private requestState = inject(RequestStateService);

  email = '';
  password = '';
  showPassword = signal(false);

  // Delegates to shared state signals — templates remain unchanged
  get error() { return this.requestState.error; }
  get loading() { return this.requestState.loading; }

  login(form: NgForm) {
    form.control.markAllAsTouched();

    if (form.invalid) {
      this.requestState.error.set('Please fix the errors below before submitting.');
      return;
    }
    this.requestState.reset();

    this.authService.login(this.email.trim(), this.password).subscribe({
      next: () => {
        const role = this.authService.getRole();
        const staffRoles = ['ADMIN', 'AGENT', 'CLAIMS_OFFICER'];
        if (!role || !staffRoles.includes(role)) {
          localStorage.removeItem('relief_token');
          this.requestState.error.set('This account is not a staff account. Please use Customer Login.');
          return;
        }
        if (role === 'AGENT') {
          this.router.navigate(['/agent/dashboard']);
        } else if (role === 'CLAIMS_OFFICER') {
          this.router.navigate(['/claims-officer/dashboard']);
        } else {
          this.router.navigate(['/admin/dashboard']);
        }
      },
      error: (err) => {
        this.requestState.error.set(err.error?.error || 'Invalid email or password. Please try again.');
      }
    });
  }
}
