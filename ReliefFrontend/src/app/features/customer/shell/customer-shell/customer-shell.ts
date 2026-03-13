import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';

@Component({
  selector: 'app-customer-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './customer-shell.html',
  styleUrl: './customer-shell.css',
})
export class CustomerShell {
  sidebarOpen = signal(true);
  userName = localStorage.getItem('relief_user_name') || 'Customer';

  navItems = [
    { label: 'Dashboard', icon: 'dashboard', route: '/customer/dashboard' },
    { label: 'My Policies', icon: 'policy', route: '/customer/policies' },
    { label: 'My Claims', icon: 'description', route: '/customer/claims' },
    { label: 'Payments', icon: 'payments', route: '/customer/payments' },
  ];

  constructor(private router: Router) {}

  logout() {
    localStorage.clear();
    this.router.navigate(['/customer-login']);
  }
}
