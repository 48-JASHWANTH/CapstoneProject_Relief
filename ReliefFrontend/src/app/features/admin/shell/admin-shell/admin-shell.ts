import { Component, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../../core/services/auth';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-admin-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.css',
})
export class AdminShell {
  sidebarOpen = signal(true);

  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/admin/dashboard' },
    { label: 'Users', icon: 'people', route: '/admin/users' },
    { label: 'Policies', icon: 'policy', route: '/admin/policies' },
    { label: 'Claims', icon: 'assignment', route: '/admin/claims' },
    { label: 'Disaster Zones', icon: 'public', route: '/admin/disaster-zones' },
    { label: 'Risk Pools', icon: 'account_balance', route: '/admin/risk-pools' },
  ];

  constructor(private auth: AuthService, private router: Router) {}

  get userName(): string {
    return this.auth.getUserEmail();
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/staff-login']);
  }
}
