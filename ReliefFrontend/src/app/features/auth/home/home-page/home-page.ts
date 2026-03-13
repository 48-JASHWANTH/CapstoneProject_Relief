import { Component, HostListener, OnInit, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink, CommonModule],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePage implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);
  loginOpen = false;

  ngOnInit() {
    const role = this.authService.getRole();
    if (role) {
      if (role === 'ADMIN') this.router.navigate(['/admin/dashboard']);
      else if (role === 'AGENT') this.router.navigate(['/agent/dashboard']);
      else if (role === 'CLAIMS_OFFICER') this.router.navigate(['/claims-officer/dashboard']);
      else if (role === 'CUSTOMER') this.router.navigate(['/customer/dashboard']);
    }
  }

  toggleLogin() { this.loginOpen = !this.loginOpen; }

  scrollTo(id: string) {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent) {
    const el = e.target as HTMLElement;
    if (!el.closest('#login-menu')) this.loginOpen = false;
  }
}
