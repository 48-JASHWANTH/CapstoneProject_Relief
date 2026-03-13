import { Component, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../../core/services/auth';

interface NavItem { label: string; icon: string; route: string; }

@Component({
  selector: 'app-agent-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './agent-shell.html',
  styleUrl: './agent-shell.css',
})
export class AgentShell {
  sidebarOpen = signal(true);
  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/agent/dashboard' },
    { label: 'My Policies', icon: 'policy', route: '/agent/policies' },
  ];

  constructor(private auth: AuthService, private router: Router) {}

  get userName(): string { return this.auth.getUserEmail(); }
  toggleSidebar(): void { this.sidebarOpen.update(v => !v); }
  logout(): void { this.auth.logout(); this.router.navigate(['/staff-login']); }
}
