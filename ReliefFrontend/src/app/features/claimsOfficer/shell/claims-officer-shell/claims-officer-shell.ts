import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';

@Component({
  selector: 'app-claims-officer-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './claims-officer-shell.html',
  styleUrl: './claims-officer-shell.css',
})
export class ClaimsOfficerShell {
  sidebarOpen = signal(true);
  userName = localStorage.getItem('relief_user_name') || 'Officer';

  navItems = [
    { label: 'Dashboard', icon: 'dashboard', route: '/claims-officer/dashboard' },
    { label: 'All Claims', icon: 'assignment', route: '/claims-officer/claims' },
  ];

  constructor(private router: Router) {}

  logout() {
    localStorage.clear();
    this.router.navigate(['/staff-login']);
  }
}
