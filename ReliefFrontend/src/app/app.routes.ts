import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  {
    path: 'home',
    loadComponent: () => import('./features/auth/home/home-page/home-page').then(m => m.HomePage),
  },
  {
    path: 'staff-login',
    loadComponent: () => import('./features/auth/staff-login/staff-login').then(m => m.StaffLogin),
  },
  {
    path: 'customer-login',
    loadComponent: () => import('./features/auth/customer-login/customer-login').then(m => m.CustomerLogin),
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then(m => m.Register),
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./features/admin/shell/admin-shell/admin-shell').then(m => m.AdminShell),
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/dashboard/components/admin-dashboard/admin-dashboard').then(
            m => m.AdminDashboard
          ),
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/admin/users/components/admin-users/admin-users').then(m => m.AdminUsers),
      },

      {
        path: 'policies',
        loadComponent: () =>
          import('./features/admin/policies/components/admin-policies/admin-policies').then(
            m => m.AdminPolicies
          ),
      },
      {
        path: 'policies/:id',
        loadComponent: () =>
          import(
            './features/admin/policies/components/admin-policy-detail/admin-policy-detail'
          ).then(m => m.AdminPolicyDetail),
      },
      {
        path: 'disaster-zones',
        loadComponent: () =>
          import(
            './features/admin/disaster-zones/components/admin-disaster-zones/admin-disaster-zones'
          ).then(m => m.AdminDisasterZones),
      },
      {
        path: 'risk-pools',
        loadComponent: () =>
          import('./features/admin/risk-pools/components/admin-risk-pools/admin-risk-pools').then(
            m => m.AdminRiskPools
          ),
      },

      {
        path: 'claims',
        loadComponent: () =>
          import('./features/admin/claims/components/admin-claims/admin-claims').then(m => m.AdminClaims),
      },
    ],
  },
  {
    path: 'agent',
    loadComponent: () =>
      import('./features/agent/shell/agent-shell/agent-shell').then(m => m.AgentShell),
    canActivate: [authGuard, roleGuard],
    data: { role: 'AGENT' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/agent/dashboard/components/agent-dashboard/agent-dashboard').then(m => m.AgentDashboard),
      },
      {
        path: 'policies',
        loadComponent: () =>
          import('./features/agent/policies/components/agent-policies/agent-policies').then(m => m.AgentPolicies),
      },
      {
        path: 'policies/:id',
        loadComponent: () =>
          import('./features/agent/policies/components/agent-policy-detail/agent-policy-detail').then(m => m.AgentPolicyDetail),
      },

    ],
  },
  {
    path: 'claims-officer',
    loadComponent: () =>
      import('./features/claimsOfficer/shell/claims-officer-shell/claims-officer-shell').then(m => m.ClaimsOfficerShell),
    canActivate: [authGuard, roleGuard],
    data: { role: 'CLAIMS_OFFICER' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/claimsOfficer/dashboard/components/claims-officer-dashboard/claims-officer-dashboard').then(m => m.ClaimsOfficerDashboard),
      },
      {
        path: 'claims',
        loadComponent: () =>
          import('./features/claimsOfficer/claims/components/claims-officer-claims/claims-officer-claims').then(m => m.ClaimsOfficerClaims),
      },
      {
        path: 'claims/:id',
        loadComponent: () =>
          import('./features/claimsOfficer/claims/components/claims-officer-claim-detail/claims-officer-claim-detail').then(m => m.ClaimsOfficerClaimDetail),
      },
    ],
  },
  {
    path: 'customer',
    loadComponent: () =>
      import('./features/customer/shell/customer-shell/customer-shell').then(m => m.CustomerShell),
    canActivate: [authGuard, roleGuard],
    data: { role: 'CUSTOMER' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/customer/dashboard/components/customer-dashboard/customer-dashboard').then(m => m.CustomerDashboard),
      },
      {
        path: 'policies/new',
        loadComponent: () =>
          import('./features/customer/policies/components/customer-new-policy/customer-new-policy').then(m => m.CustomerNewPolicy),
      },
      {
        path: 'policies/:id',
        loadComponent: () =>
          import('./features/customer/policies/components/customer-policy-detail/customer-policy-detail').then(m => m.CustomerPolicyDetail),
      },
      {
        path: 'policies',
        loadComponent: () =>
          import('./features/customer/policies/components/customer-policies/customer-policies').then(m => m.CustomerPolicies),
      },
      {
        path: 'claims/new',
        loadComponent: () =>
          import('./features/customer/claims/components/customer-new-claim/customer-new-claim').then(m => m.CustomerNewClaim),
      },
      {
        path: 'claims/:id',
        loadComponent: () =>
          import('./features/customer/claims/components/customer-claim-detail/customer-claim-detail').then(m => m.CustomerClaimDetail),
      },
      {
        path: 'claims',
        loadComponent: () =>
          import('./features/customer/claims/components/customer-claims/customer-claims').then(m => m.CustomerClaims),
      },
      {
        path: 'payments',
        loadComponent: () =>
          import('./features/customer/payments/components/customer-payments/customer-payments').then(m => m.CustomerPayments),
      },
    ],
  },
  { path: '**', redirectTo: 'home' },
];
