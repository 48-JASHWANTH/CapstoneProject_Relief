import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { RequestStateService } from '../../../core/services/request-state.service';

@Component({
  selector: 'app-customer-login',
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './customer-login.html',
  styleUrl: './customer-login.css',
})
export class CustomerLogin {
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
      this.requestState.error.set('Please fill all the fields before submitting.');
      return;
    }
    this.requestState.reset();

    this.authService.login(this.email.trim(), this.password).subscribe({
      next: () => {
        const role = this.authService.getRole();
        if (role === 'CUSTOMER') {
          this.router.navigate(['/customer/dashboard']);
        } else {
          localStorage.removeItem('relief_token');
          this.requestState.error.set('This account is not a customer account. Please use Staff Login.');
        }
      },
      error: (err) => {
        this.requestState.error.set(err.error?.error || 'Invalid email or password. Please try again.');
      }
    });
  }
}
