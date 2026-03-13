# RELIEF INSURANCE — ANGULAR FRONTEND GENERATION PROMPTS

---

# ═══════════════════════════════════════════════
# PART 1 — MASTER PROMPT (Everything in One Shot)
# ═══════════════════════════════════════════════

Build a complete Angular 17+ (standalone components) frontend for the **Relief Insurance Management System** — a disaster-relief insurance platform. The backend is Spring Boot + JWT. **Do NOT connect to the backend at all.** All data must come from **Angular services using static mock data** defined inside service files — never in HTML templates or component `.ts` files.

---

## Technology Stack
- Angular 17+ standalone components
- Angular Router with `router-outlet` for all content areas
- Angular Reactive Forms (FormGroup, FormControl, Validators)
- Angular Material UI (mat-table, mat-paginator, mat-sort, mat-dialog, mat-snackbar, mat-sidenav, mat-toolbar, mat-card, mat-chips, mat-select, mat-datepicker)
- `ng2-charts` (Chart.js wrapper) for all dashboard charts
- `jwt-decode` library to decode mock JWT tokens
- `rxjs` `of()` + `delay(300)` to simulate async service calls
- `localStorage` for storing mock auth token and user role

---

## Two Login Pages

### Login Page 1 — Staff Portal (`/staff-login`)
- Roles: ADMIN, AGENT, CLAIMS_OFFICER
- Form fields matching backend DTO `JwtRequest`: `email: string`, `password: string`
- On submit → mock auth service checks credentials from a hardcoded users list → stores `relief_token` and `relief_role` and `relief_user_id` in localStorage → navigates based on role:
    - ADMIN → `/admin/dashboard`
    - AGENT → `/agent/dashboard`
    - CLAIMS_OFFICER → `/claims-officer/dashboard`
- Seed credentials (match backend DataInitializer):
    - admin@relief.com / admin123 → ADMIN
    - john@relief.com / agent123 → AGENT (agentId=1)
    - sara@relief.com / officer123 → CLAIMS_OFFICER
- UI: Dark navy (#1e3a5f) branded login card, "Relief — Staff Portal" heading, company shield icon

### Login Page 2 — Customer Portal (`/customer-login`)
- Role: CUSTOMER only
- Same form fields: `email`, `password`
- Has a "New here? Register" link → `/register`
- Seed credentials:
    - alice@relief.com / customer123 → CUSTOMER (userId=2)
    - bob@relief.com / customer123 → CUSTOMER (userId=3)
- On submit → `/customer/dashboard`
- UI: Softer blue-green branded login card, "Relief — Customer Portal" heading

### Register Page (`/register`)
- Form matching backend DTO `RegisterRequest`: `name: string`, `email: string`, `password: string`
- Plus `confirmPassword` field (frontend only, validation only)
- Validators: all required, email format, password min 6 chars, password match
- On success: mock "registered" → redirect to `/customer-login`
- Show validation error messages below each field

---

## Application Shell (Post-Login Layout)

All authenticated pages share this layout:
- **Left Sidenav** (`mat-sidenav`, fixed, 250px): Logo at top, role-specific nav links with Material icons
- **Top Toolbar** (`mat-toolbar`): Page title (left), user full name + role badge + logout button (right)
- **Content area**: `<router-outlet>` fills remaining space with scroll

Shell component: `AppShellComponent` used by all role shells (AdminShellComponent, AgentShellComponent, ClaimsOfficerShellComponent, CustomerShellComponent). Each shell passes its nav items to the shared shell.

---

## Role 1: ADMIN

**Purpose**: Full system oversight — manages users, agents, policies, disaster zones, risk pools, and roles.

**Sidebar Nav Links** (with icons):
1. Dashboard (dashboard icon)
2. Users (people icon)
3. Agents (support_agent icon)
4. Policies (policy icon)
5. Disaster Zones (public icon)
6. Risk Pools (account_balance icon)
7. Roles (admin_panel_settings icon)

### Admin — Dashboard (`/admin/dashboard`)
**Service**: `admin-dashboard.service.ts` — returns mock `AdminDashboardResponse`
- **Stat Cards Row 1**: Total Users, Total Agents, Total Policies, Total Claims
- **Stat Cards Row 2**: Total Payments, Disaster Zones, Risk Pools, Critical Risk Pools
- **Stat Cards Row 3**: Active Policies, Pending Policies, Approved Claims, Pending Claims
- **Bar Chart**: Policies by Disaster Type (`policiesByDisasterType` map → labels + values)
- **Pie Chart**: Claims by Status (`claimsByStatus` map → labels + values)
- **Risk Pool Summary Table**: columns: disasterType, totalPremiumCollected, totalClaimsPaid, poolStatus, criticalFlag (red badge if true)

Mock data must populate all fields of `AdminDashboardResponse`:
```
totalUsers=5, totalAgents=1, totalPolicies=4, totalClaims=1, totalPayments=1,
totalDisasterZones=3, totalRiskPools=3, activePolicies=1, pendingPolicies=1,
approvedClaims=0, pendingClaims=1, criticalRiskPools=0,
policiesByDisasterType={FLOOD:2, EARTHQUAKE:1, CYCLONE:1},
claimsByStatus={FILED:1},
riskPoolSummary={FLOOD:10920.0, EARTHQUAKE:0.0, CYCLONE:0.0}
```

### Admin — Users (`/admin/users`)
**Service**: `admin-user.service.ts`
**Mock data** — 5 users (UserResponse):
```
id:1, name:"Admin User", email:"admin@relief.com", status:"ACTIVE", roles:["ADMIN"], createdAt: "2026-01-01T00:00:00"
id:2, name:"John Smith", email:"john@relief.com", status:"ACTIVE", roles:["AGENT"], createdAt: "2026-01-01T00:00:00"
id:3, name:"Alice Johnson", email:"alice@relief.com", status:"ACTIVE", roles:["CUSTOMER"], createdAt: "2026-01-01T00:00:00"
id:4, name:"Bob Williams", email:"bob@relief.com", status:"ACTIVE", roles:["CUSTOMER"], createdAt: "2026-01-01T00:00:00"
id:5, name:"Sara Davis", email:"sara@relief.com", status:"ACTIVE", roles:["CLAIMS_OFFICER"], createdAt: "2026-01-01T00:00:00"
```
**Table columns**: id, name, email, status (chip), roles (chips), createdAt, Actions
**Actions**:
- **Change Status**: dialog with dropdown ACTIVE/INACTIVE → calls `PATCH /api/admin/users/{id}/status` with `{ status }` → updates mock array
- **Assign Role**: dialog with multi-select of available roles + userId → calls `POST /api/admin/users/assign-roles` with `{ userId, roleNames: Set<string> }`
- **Remove Role**: icon button → calls `DELETE /api/admin/users/{id}/roles/{roleName}`
- **Delete User**: confirm dialog → calls `DELETE /api/admin/users/{id}`
  **Filters**: dropdown filter by role (ADMIN/AGENT/CUSTOMER/CLAIMS_OFFICER), dropdown filter by status (ACTIVE/INACTIVE)
  **Search**: by name or email

### Admin — Agents (`/admin/agents`)
**Service**: `admin-agent.service.ts`
**Mock data** — 1 agent (AgentResponse):
```
id:1, userId:2, userName:"John Smith", userEmail:"john@relief.com", licenseNumber:"LIC-001", region:"NORTH", totalPolicies:4
```
**Table columns**: id, userName, userEmail, licenseNumber, region, totalPolicies, Actions
**Actions**:
- **Create Agent**: form dialog with `AgentRequest`: `{ userId: number, licenseNumber: string, region: string }` → adds to mock array
- **Edit Agent**: same form dialog pre-filled → updates mock array
- **Delete Agent**: confirm dialog → removes from mock array
  **Filter**: by region (NORTH/SOUTH/EAST/WEST/CENTRAL)

### Admin — Policies (`/admin/policies`)
**Service**: `admin-policy.service.ts`
**Mock data** — 4 policies (PolicyResponse):
```
{id:1, policyNumber:"POL-ALICE-001", userId:3, userName:"Alice Johnson", agentId:1, agentName:"John Smith", disasterType:"FLOOD", policyType:"STANDARD", propertyAddress:"12 Riverside Lane, North Region", propertyValue:200000, sumInsured:150000, premiumAmount:3900, status:"APPROVED", remarks:"Approved after underwriting review.", startDate:"2026-01-01", endDate:"2027-01-01", disasterZoneName:"North Flood Zone", riskPoolDisasterType:"FLOOD"},
{id:2, policyNumber:"POL-ALICE-002", userId:3, userName:"Alice Johnson", agentId:1, agentName:"John Smith", disasterType:"EARTHQUAKE", policyType:"BASIC", propertyAddress:"12 Riverside Lane, North Region", propertyValue:200000, sumInsured:100000, premiumAmount:1500, status:"PENDING", remarks:null, startDate:"2026-02-01", endDate:"2027-02-01", disasterZoneName:"Central Seismic Zone", riskPoolDisasterType:"EARTHQUAKE"},
{id:3, policyNumber:"POL-BOB-001", userId:4, userName:"Bob Williams", agentId:1, agentName:"John Smith", disasterType:"FLOOD", policyType:"PREMIUM", propertyAddress:"88 Hillcrest Ave, North Region", propertyValue:350000, sumInsured:280000, premiumAmount:10920, status:"ACTIVE", remarks:"Premium paid. Policy active.", startDate:"2026-01-15", endDate:"2027-01-15", disasterZoneName:"North Flood Zone", riskPoolDisasterType:"FLOOD"},
{id:4, policyNumber:"POL-BOB-002", userId:4, userName:"Bob Williams", agentId:1, agentName:"John Smith", disasterType:"CYCLONE", policyType:"STANDARD", propertyAddress:"88 Hillcrest Ave, North Region", propertyValue:350000, sumInsured:200000, premiumAmount:5600, status:"UNDER_REVIEW", remarks:"Forwarded for admin approval. Cyclone exposure verified.", startDate:"2026-03-01", endDate:"2027-03-01", disasterZoneName:"Coastal Cyclone Zone", riskPoolDisasterType:"CYCLONE"}
```
**Table columns**: policyNumber, userName, agentName, disasterType, policyType, propertyValue, sumInsured, premiumAmount, status (chip), startDate, endDate, Actions
**Actions**:
- **Approve/Reject**: dialog with `PolicyApprovalRequest`: `{ status: 'APPROVED'|'REJECTED', remarks: string }` → updates mock array
- **View Detail**: navigate to `/admin/policies/:id`
  **Filters**: by status, by disasterType

### Admin — Disaster Zones (`/admin/disaster-zones`)
**Service**: `admin-disaster-zone.service.ts`
**Mock data** — 3 zones (DisasterZoneResponse):
```
{id:1, zoneName:"North Flood Zone", location:"North Region", riskLevel:"HIGH", disasterType:"FLOOD", totalPolicies:2},
{id:2, zoneName:"Central Seismic Zone", location:"Central Region", riskLevel:"MEDIUM", disasterType:"EARTHQUAKE", totalPolicies:1},
{id:3, zoneName:"Coastal Cyclone Zone", location:"South Coast", riskLevel:"HIGH", disasterType:"CYCLONE", totalPolicies:1}
```
**Table columns**: id, zoneName, location, riskLevel (chip), disasterType, totalPolicies, Actions
**Actions**: Create (form dialog: `{ zoneName, location, riskLevel, disasterType }`), Edit, Delete
**Filters**: by riskLevel (HIGH/MEDIUM/LOW), by disasterType

### Admin — Risk Pools (`/admin/risk-pools`)
**Service**: `admin-risk-pool.service.ts`
**Mock data** — 3 risk pools (RiskPoolResponse):
```
{id:1, disasterType:"FLOOD", totalPremiumCollected:10920, totalClaimsPaid:0, thresholdPercentage:75, poolStatus:"HEALTHY", criticalFlag:false, totalPolicies:2, totalClaims:1},
{id:2, disasterType:"EARTHQUAKE", totalPremiumCollected:0, totalClaimsPaid:0, thresholdPercentage:70, poolStatus:"HEALTHY", criticalFlag:false, totalPolicies:1, totalClaims:0},
{id:3, disasterType:"CYCLONE", totalPremiumCollected:0, totalClaimsPaid:0, thresholdPercentage:80, poolStatus:"HEALTHY", criticalFlag:false, totalPolicies:1, totalClaims:0}
```
**Table columns**: id, disasterType, totalPremiumCollected, totalClaimsPaid, thresholdPercentage, poolStatus (chip), criticalFlag, totalPolicies, totalClaims, Actions
**Actions**: Create (`{ disasterType, totalPremiumCollected, totalClaimsPaid, thresholdPercentage, poolStatus }`), Edit, Delete, Evaluate Threshold (inline button → updates poolStatus)

### Admin — Roles (`/admin/roles`)
**Service**: `admin-role.service.ts`
**Mock data** — 4 roles (RoleResponse):
```
{id:1, name:"ADMIN", description:"System administrator"},
{id:2, name:"AGENT", description:"Underwriter / agent"},
{id:3, name:"CUSTOMER", description:"Policyholder"},
{id:4, name:"CLAIMS_OFFICER", description:"Claims processing officer"}
```
**Table columns**: id, name, description, Actions
**Actions**: Create (`{ name, description }`), Edit, Delete

---

## Role 2: AGENT (Underwriter)

**Purpose**: Review and underwrite policies assigned to them. Adjust premiums, forward for admin approval. View claims on their policies.

**Sidebar Nav Links**:
1. Dashboard (dashboard icon)
2. My Policies (policy icon)
3. Claims on My Policies (assignment icon)

### Agent — Dashboard (`/agent/dashboard`)
**Service**: `agent-dashboard.service.ts` — returns mock `AgentDashboardResponse`
Mock data:
```
agentId:1, agentName:"John Smith", licenseNumber:"LIC-001", region:"NORTH",
totalPoliciesAssigned:4, pendingPolicies:1, approvedPolicies:1, rejectedPolicies:0, activePolicies:1,
totalClaims:1, approvedClaims:0, pendingClaims:1, rejectedClaims:0,
policiesByDisasterType:{FLOOD:2, EARTHQUAKE:1, CYCLONE:1},
lossFrequencyByDisasterType:{FLOOD:75000.0, EARTHQUAKE:0.0, CYCLONE:0.0},
approvalRatio:25.0,
recentPolicies: [first 3 from policy mock data],
recentClaims: [1 claim mock]
```
**Stat Cards**: totalPoliciesAssigned, pendingPolicies, approvedPolicies, activePolicies, totalClaims, pendingClaims
**Bar chart**: policiesByDisasterType
**Donut chart**: approvalRatio (approved % vs rest)
**Bar chart**: lossFrequencyByDisasterType
**Recent Policies table** (compact): policyNumber, userName, disasterType, status
**Recent Claims table** (compact): claimNumber, policyNumber, estimatedLoss, status

### Agent — My Policies (`/agent/policies`)
**Service**: `agent-policy.service.ts`
Mock data: same 4 policies as admin
**Table columns**: policyNumber, userName, disasterType, policyType, propertyAddress, sumInsured, premiumAmount, status (chip), Actions
**Actions**:
- **Adjust Premium**: dialog form — `AgentPremiumAdjustRequest`: `{ adjustedPremium: number, remarks: string }` → updates premiumAmount + remarks in mock array
- **Forward for Approval**: dialog form — `AgentForwardPolicyRequest`: `{ remarks: string }` → updates status to FORWARDED, remarks in mock array. Only available for PENDING policies.
- **View Detail**: navigate to `/agent/policies/:id`
  **Filter**: by status

### Agent — My Policy Detail (`/agent/policies/:id`)
Shows all PolicyResponse fields in a detail card layout. Shows remarks. Shows associated claim count (from claims mock).

### Agent — Claims on My Policies (`/agent/claims`)
**Service**: `agent-claims.service.ts`
Mock data: same claims as below (1 claim initially)
**Table columns**: claimNumber, policyNumber, description, estimatedLoss, approvedAmount, status (chip), filedDate, resolvedDate
**Read-only** — no actions
**Filter**: by status

---

## Role 3: CLAIMS OFFICER

**Purpose**: Review all insurance claims system-wide. Mark claims under review. Approve or reject claims with payout decisions.

**Sidebar Nav Links**:
1. Dashboard (dashboard icon)
2. All Claims (list_alt icon)
3. High-Value Claims (priority_high icon)

### Claims Officer — Dashboard (`/claims-officer/dashboard`)
**Service**: `claims-officer-dashboard.service.ts` — returns mock `ClaimsOfficerDashboardResponse`
Mock data:
```
totalClaims:1, filedClaims:1, underReviewClaims:0, approvedClaims:0, rejectedClaims:0, paidClaims:0,
totalApprovedAmount:0.0, totalPaidAmount:0.0, highValueClaimsCount:1,
claimsByDisasterType:{FLOOD:1},
claimsByStatus:{FILED:1},
riskPoolSnapshot: [all 3 risk pools],
pendingAttentionClaims: [1 claim: CLM-BOB-001]
```
**Stat Cards**: totalClaims, filedClaims, underReviewClaims, approvedClaims, rejectedClaims, paidClaims, totalApprovedAmount, totalPaidAmount, highValueClaimsCount
**Pie chart**: claimsByDisasterType
**Bar chart**: claimsByStatus
**Risk Pool Snapshot Table**: disasterType, totalPremiumCollected, totalClaimsPaid, poolStatus, criticalFlag
**Pending Attention Claims list**: compact list of claimNumber, policyNumber, estimatedLoss, status, filedDate with "Review" button

### Claims Officer — All Claims (`/claims-officer/claims`)
**Service**: `claims-officer-claims.service.ts`
Mock data — 1 claim (ClaimResponse):
```
{id:1, claimNumber:"CLM-BOB-001", policyId:3, policyNumber:"POL-BOB-001", description:"Severe flooding damaged ground floor and basement. Water level reached 4 feet.", estimatedLoss:75000, approvedAmount:null, status:"FILED", officerRemarks:null, filedDate:"2026-02-10T09:30:00", resolvedDate:null}
```
**Table columns**: claimNumber, policyNumber, description (truncated), estimatedLoss, approvedAmount, status (chip), filedDate, resolvedDate, Actions
**Actions**:
- **Mark Under Review**: button (only for FILED claims) → updates status to UNDER_REVIEW in mock array
- **Decide on Claim**: dialog form — `ClaimDecisionRequest`: `{ decision: 'APPROVED'|'REJECTED', approvedAmount: number, remarks: string }` → updates claim in mock array. Only for UNDER_REVIEW claims.
- **View Detail**: navigate to `/claims-officer/claims/:id`
  **Filters**: by status, by disasterType (resolved from policyNumber lookup)

### Claims Officer — Claim Detail (`/claims-officer/claims/:id`)
Full detail view: all ClaimResponse fields + linked PolicyResponse fields (policyNumber, disasterType, propertyAddress, sumInsured) + action buttons (same as list actions)

### Claims Officer — High-Value Claims (`/claims-officer/high-value`)
**Service**: `high-value-claims.service.ts`
Same claims data filtered where `estimatedLoss > 50000`
Same table columns and actions as All Claims
Threshold input at top: number input, default 50000, filters list dynamically

---

## Role 4: CUSTOMER

**Purpose**: Apply for insurance policies, pay premiums, file claims, track everything.

**Sidebar Nav Links**:
1. Dashboard (dashboard icon)
2. My Policies (policy icon)
3. Apply for Policy (add_circle icon)
4. My Claims (assignment icon)
5. File a Claim (report icon)
6. My Payments (payment icon)

### Customer — Dashboard (`/customer/dashboard`)
**Service**: `customer-dashboard.service.ts` — returns mock `UserDashboardResponse`
Customer: Alice Johnson (userId=3) by default when alice is logged in, Bob for Bob.
Mock data for Alice:
```
userId:3, name:"Alice Johnson", email:"alice@relief.com",
totalPolicies:2, activePolicies:0, pendingPolicies:1,
totalClaims:0, approvedClaims:0, pendingClaims:0, rejectedClaims:0,
totalPayments:0, totalPremiumPaid:0.0,
policies: [POL-ALICE-001, POL-ALICE-002],
claims: [],
payments: []
```
Mock data for Bob:
```
userId:4, name:"Bob Williams", email:"bob@relief.com",
totalPolicies:2, activePolicies:1, pendingPolicies:0,
totalClaims:1, approvedClaims:0, pendingClaims:1, rejectedClaims:0,
totalPayments:0, totalPremiumPaid:0.0,
policies: [POL-BOB-001, POL-BOB-002],
claims: [CLM-BOB-001],
payments: []
```
**Stat Cards**: totalPolicies, activePolicies, pendingPolicies, totalClaims, pendingClaims, totalPremiumPaid
**Recent Policies table** (compact): policyNumber, disasterType, status
**Recent Claims table** (compact): claimNumber, status, estimatedLoss
**Recent Payments table** (compact): policyNumber, amount, paymentStatus

### Customer — My Policies (`/customer/policies`)
**Service**: `customer-policy.service.ts`
Mock data: Alice sees POL-ALICE-001 and POL-ALICE-002; Bob sees POL-BOB-001 and POL-BOB-002
**Table columns**: policyNumber, disasterType, policyType, propertyAddress, sumInsured, premiumAmount, status (chip), startDate, endDate, Actions
**Actions**:
- **Pay Premium**: button (only for APPROVED policies) → dialog confirms payment → calls mock with `PremiumPaymentRequest`: `{ policyId: number }` → adds a new PaymentResponse to payment mock, updates policy status to ACTIVE
- **View Detail**: navigate to `/customer/policies/:id`
  **Filter**: by status

### Customer — Apply for Policy (`/customer/policies/new`)
**Form** matching `UserPolicyRequest`:
```typescript
{
  disasterType: string,  // dropdown: FLOOD | EARTHQUAKE | CYCLONE | HURRICANE
  policyType: string,    // dropdown: BASIC | STANDARD | PREMIUM
  propertyAddress: string,
  propertyValue: number,
  sumInsured: number,
  startDate: string,     // date picker (ISO format YYYY-MM-DD)
  endDate: string        // date picker
}
```
Validators: all required, propertyValue > 0, sumInsured > 0, sumInsured <= propertyValue, endDate > startDate
On submit → adds new PolicyResponse to mock array with status="PENDING", auto-generates policyNumber like "POL-NEW-001" → shows success snackbar → navigate to `/customer/policies`

### Customer — Policy Detail (`/customer/policies/:id`)
Full detail card: all PolicyResponse fields. If status=APPROVED → show Pay Premium button.

### Customer — My Claims (`/customer/claims`)
**Service**: `customer-claims.service.ts`
Mock data: Alice sees no claims; Bob sees CLM-BOB-001
**Table columns**: claimNumber, policyNumber, description, estimatedLoss, approvedAmount, status (chip), officerRemarks, filedDate, resolvedDate
**Actions**: View Detail only
**Filter**: by status

### Customer — File a Claim (`/customer/claims/new`)
**Form** matching `ClaimRequest`:
```typescript
{
  policyId: number,      // dropdown of user's ACTIVE/APPROVED policies
  description: string,   // textarea, min 20 chars
  estimatedLoss: number  // positive number
}
```
Validators: all required, estimatedLoss > 0, description minLength 20
On submit → adds new ClaimResponse to mock array with status="FILED", auto-generates claimNumber → success snackbar → navigate to `/customer/claims`

### Customer — My Payments (`/customer/payments`)
**Service**: `customer-payment.service.ts`
Mock data: initially empty, populated after Pay Premium actions
**Table columns**: id, policyNumber, paymentType, amount, paymentStatus (chip), paymentDate

---

## Complete Routing (`app.routes.ts`)

```typescript
export const routes: Routes = [
  { path: '', redirectTo: 'customer-login', pathMatch: 'full' },
  { path: 'staff-login', loadComponent: () => import('./features/auth/staff-login/staff-login.component').then(m => m.StaffLoginComponent) },
  { path: 'customer-login', loadComponent: () => import('./features/auth/customer-login/customer-login.component').then(m => m.CustomerLoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },

  {
    path: 'admin',
    loadComponent: () => import('./features/admin/shell/admin-shell.component').then(m => m.AdminShellComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ADMIN' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/admin/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
      { path: 'users', loadComponent: () => import('./features/admin/users/admin-users.component').then(m => m.AdminUsersComponent) },
      { path: 'users/:id', loadComponent: () => import('./features/admin/users/admin-user-detail.component').then(m => m.AdminUserDetailComponent) },
      { path: 'agents', loadComponent: () => import('./features/admin/agents/admin-agents.component').then(m => m.AdminAgentsComponent) },
      { path: 'policies', loadComponent: () => import('./features/admin/policies/admin-policies.component').then(m => m.AdminPoliciesComponent) },
      { path: 'policies/:id', loadComponent: () => import('./features/admin/policies/admin-policy-detail.component').then(m => m.AdminPolicyDetailComponent) },
      { path: 'disaster-zones', loadComponent: () => import('./features/admin/disaster-zones/admin-disaster-zones.component').then(m => m.AdminDisasterZonesComponent) },
      { path: 'risk-pools', loadComponent: () => import('./features/admin/risk-pools/admin-risk-pools.component').then(m => m.AdminRiskPoolsComponent) },
      { path: 'roles', loadComponent: () => import('./features/admin/roles/admin-roles.component').then(m => m.AdminRolesComponent) },
    ]
  },

  {
    path: 'agent',
    loadComponent: () => import('./features/agent/shell/agent-shell.component').then(m => m.AgentShellComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'AGENT' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/agent/dashboard/agent-dashboard.component').then(m => m.AgentDashboardComponent) },
      { path: 'policies', loadComponent: () => import('./features/agent/policies/agent-policies.component').then(m => m.AgentPoliciesComponent) },
      { path: 'policies/:id', loadComponent: () => import('./features/agent/policies/agent-policy-detail.component').then(m => m.AgentPolicyDetailComponent) },
      { path: 'claims', loadComponent: () => import('./features/agent/claims/agent-claims.component').then(m => m.AgentClaimsComponent) },
    ]
  },

  {
    path: 'claims-officer',
    loadComponent: () => import('./features/claimsOfficer/shell/claims-officer-shell.component').then(m => m.ClaimsOfficerShellComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'CLAIMS_OFFICER' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/claimsOfficer/dashboard/claims-officer-dashboard.component').then(m => m.ClaimsOfficerDashboardComponent) },
      { path: 'claims', loadComponent: () => import('./features/claimsOfficer/claims/claims-officer-claims.component').then(m => m.ClaimsOfficerClaimsComponent) },
      { path: 'claims/:id', loadComponent: () => import('./features/claimsOfficer/claims/claims-officer-claim-detail.component').then(m => m.ClaimsOfficerClaimDetailComponent) },
      { path: 'high-value', loadComponent: () => import('./features/claimsOfficer/high-value/high-value-claims.component').then(m => m.HighValueClaimsComponent) },
    ]
  },

  {
    path: 'customer',
    loadComponent: () => import('./features/customer/shell/customer-shell.component').then(m => m.CustomerShellComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'CUSTOMER' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/customer/dashboard/customer-dashboard.component').then(m => m.CustomerDashboardComponent) },
      { path: 'policies', loadComponent: () => import('./features/customer/policies/customer-policies.component').then(m => m.CustomerPoliciesComponent) },
      { path: 'policies/new', loadComponent: () => import('./features/customer/policies/customer-new-policy.component').then(m => m.CustomerNewPolicyComponent) },
      { path: 'policies/:id', loadComponent: () => import('./features/customer/policies/customer-policy-detail.component').then(m => m.CustomerPolicyDetailComponent) },
      { path: 'claims', loadComponent: () => import('./features/customer/claims/customer-claims.component').then(m => m.CustomerClaimsComponent) },
      { path: 'claims/new', loadComponent: () => import('./features/customer/claims/customer-new-claim.component').then(m => m.CustomerNewClaimComponent) },
      { path: 'payments', loadComponent: () => import('./features/customer/payments/customer-payments.component').then(m => m.CustomerPaymentsComponent) },
    ]
  },

  { path: '**', redirectTo: 'customer-login' }
];
```

---

## Auth Guard Implementation
```typescript
// auth.guard.ts
canActivate(): boolean {
  const token = localStorage.getItem('relief_token');
  if (!token) { router.navigate(['/customer-login']); return false; }
  return true;
}

// role.guard.ts
canActivate(route: ActivatedRouteSnapshot): boolean {
  const required = route.data['role'];
  const current = localStorage.getItem('relief_role');
  if (current !== required) {
    if (current === 'CUSTOMER') router.navigate(['/customer-login']);
    else router.navigate(['/staff-login']);
    return false;
  }
  return true;
}
```

---

## UI Standards
- **Color Palette**: Primary = `#1e3a5f` (navy), Accent = `#f59e0b` (amber), Background = `#f8fafc`, Surface = white
- **Role color badges**: ADMIN=`#ef4444` (red), AGENT=`#3b82f6` (blue), CLAIMS_OFFICER=`#f97316` (orange), CUSTOMER=`#22c55e` (green)
- **Status chips**:
    - ACTIVE, APPROVED, PAID, COMPLETED = green
    - PENDING, FILED, UNDER_REVIEW, FORWARDED = amber/yellow
    - REJECTED, FAILED, EXPIRED = red
    - SURVEY_ASSIGNED = purple
- **Every table**: has `mat-paginator` (5, 10, 25 per page), `mat-sort` on all columns, search input at top
- **Loading**: `delay(300)` on all service `of()` calls + `mat-progress-bar` indeterminate while loading
- **Notifications**: `MatSnackBar` for all create/update/delete success and error
- **Dialogs**: all modals use `MatDialog` with form inside
- **Responsive**: `mat-sidenav` mode="side" on desktop, mode="over" on mobile (breakpoint observer)

---

## Critical Mock Data Rules
1. Data ONLY in service files, never in HTML or component `.ts`
2. All services return `Observable<T>` using `of(data).pipe(delay(300))`
3. CRUD operations update the in-memory array in the service
4. Field names MUST exactly match backend DTO (camelCase):
    - `policyNumber`, `sumInsured`, `premiumAmount`, `propertyAddress`, `disasterType`, `policyType`
    - `claimNumber`, `estimatedLoss`, `approvedAmount`, `officerRemarks`, `filedDate`, `resolvedDate`
    - `paymentType`, `paymentStatus`, `paymentDate`
    - `licenseNumber`, `totalPolicies`, `totalClaims`
    - `zoneName`, `riskLevel`, `disasterType`
    - `totalPremiumCollected`, `totalClaimsPaid`, `thresholdPercentage`, `poolStatus`, `criticalFlag`
5. Enums — Policy status: `PENDING`, `UNDER_REVIEW`, `FORWARDED`, `APPROVED`, `REJECTED`, `ACTIVE`, `EXPIRED`
6. Enums — Claim status: `FILED`, `SURVEY_ASSIGNED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `PAID`
7. Enums — Payment status: `PENDING`, `COMPLETED`, `FAILED`
8. Disaster types: `FLOOD`, `EARTHQUAKE`, `CYCLONE`, `HURRICANE`
9. Policy types: `BASIC`, `STANDARD`, `PREMIUM`
10. Risk levels: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`

---

*End of Part 1*

---
---
---

# ═══════════════════════════════════════════════════════════════
# PART 2 — DETAILED FEATURE SPECIFICATION PER ROLE
# ═══════════════════════════════════════════════════════════════

## 2.1 ADMIN MODULE — Detailed Breakdown

### Route Prefix: `/admin`
### Guard: `ADMIN` role required
### Shell: `AdminShellComponent` wraps all admin routes with sidenav + toolbar

---

#### 2.1.1 Admin Dashboard (`/admin/dashboard`)

**Component**: `AdminDashboardComponent`
**Service**: `AdminDashboardService` → method `getDashboard(): Observable<AdminDashboardResponse>`

**Layout** (top to bottom):
1. **Title**: "System Overview"
2. **Row 1 — Summary Cards (4 across)**:
    - Total Users (people icon, blue)
    - Total Agents (support_agent icon, teal)
    - Total Policies (policy icon, indigo)
    - Total Claims (assignment icon, orange)
3. **Row 2 — Summary Cards (4 across)**:
    - Total Payments (payment icon, green)
    - Disaster Zones (public icon, cyan)
    - Risk Pools (account_balance icon, purple)
    - Critical Risk Pools (warning icon, red — highlight red if > 0)
4. **Row 3 — Summary Cards (4 across)**:
    - Active Policies (check_circle icon, green)
    - Pending Policies (pending icon, yellow)
    - Approved Claims (verified icon, green)
    - Pending Claims (hourglass icon, amber)
5. **Row 4 — Charts (2 side by side)**:
    - **Bar Chart** (left): "Policies by Disaster Type" — x-axis: FLOOD, EARTHQUAKE, CYCLONE, HURRICANE; y-axis: count; bar color: navy
    - **Pie Chart** (right): "Claims by Status" — segments by status name; colors: green(APPROVED), amber(FILED), orange(UNDER_REVIEW), red(REJECTED)
6. **Row 5 — Risk Pool Summary Table**:
    - Columns: Disaster Type, Premium Collected ($), Claims Paid ($), Pool Status, Critical?
    - Row color: red background if criticalFlag=true

**Stat card component** (`StatCardComponent`):
- Props: title, value, icon, color
- Shows icon (left), title (small text), value (large bold number)

---

#### 2.1.2 Admin — User Management (`/admin/users`)

**Component**: `AdminUsersComponent`
**Service**: `AdminUserService`
**Methods**:
- `getAll(): Observable<UserResponse[]>`
- `updateStatus(id, req: {status}): Observable<UserResponse>`
- `assignRoles(req: {userId, roleNames}): Observable<UserResponse>`
- `removeRole(id, roleName): Observable<UserResponse>`
- `delete(id): Observable<void>`
- `getByRole(roleName): Observable<UserResponse[]>`
- `getByStatus(status): Observable<UserResponse[]>`

**Table**: mat-table with sort + paginator
- Columns: #, Name, Email, Status, Roles, Created At, Actions
- Status column: colored `mat-chip` (ACTIVE=green, INACTIVE=red)
- Roles column: row of small `mat-chip`s (ADMIN=red, AGENT=blue, CLAIMS_OFFICER=orange, CUSTOMER=green)
- Actions column: 3 icon buttons:
    - edit (pencil) → opens StatusDialog
    - person_add → opens AssignRoleDialog
    - delete (trash) → opens ConfirmDialog

**Search**: mat-form-field at top → filters by name or email in real time (client-side filter on observable data)

**Filter bar**: two mat-select dropdowns:
- Filter by Role: ALL / ADMIN / AGENT / CUSTOMER / CLAIMS_OFFICER
- Filter by Status: ALL / ACTIVE / INACTIVE

**StatusDialog** (MatDialog):
- Title: "Change User Status"
- mat-select: ACTIVE, INACTIVE
- Buttons: Cancel, Save → calls `updateStatus()` → shows snackbar "Status updated"

**AssignRoleDialog** (MatDialog):
- Title: "Assign Roles to [User Name]"
- mat-select multiple: options = all role names from RoleService
- Includes userId hidden field
- Sends `{ userId, roleNames: Set<string> }`
- Buttons: Cancel, Assign → calls `assignRoles()` → shows snackbar

**ConfirmDialog** (shared): "Are you sure you want to delete [name]?" — Cancel / Confirm

---

#### 2.1.3 Admin — Agent Management (`/admin/agents`)

**Component**: `AdminAgentsComponent`
**Service**: `AdminAgentService`
**Methods**:
- `getAll(): Observable<AgentResponse[]>`
- `create(req: {userId, licenseNumber, region}): Observable<AgentResponse>`
- `update(id, req): Observable<AgentResponse>`
- `delete(id): Observable<void>`
- `getByRegion(region): Observable<AgentResponse[]>`

**Table**: columns: #, Agent Name, Email, License Number, Region, Total Policies, Actions
**Actions**: edit (opens AgentFormDialog pre-filled), delete (ConfirmDialog)
**"Add Agent" button** (top right) → opens AgentFormDialog (empty)

**AgentFormDialog**:
- Title: Create Agent / Edit Agent
- Fields:
    - User ID: mat-select dropdown showing available users (from UserService, filter non-agent users ideally)
    - License Number: text input
    - Region: mat-select → NORTH, SOUTH, EAST, WEST, CENTRAL
- Sends `AgentRequest`: `{ userId, licenseNumber, region }`

**Filter**: mat-select by region

---

#### 2.1.4 Admin — Policy Management (`/admin/policies`)

**Component**: `AdminPoliciesComponent`
**Service**: `AdminPolicyService`
**Methods**:
- `getAll(): Observable<PolicyResponse[]>`
- `getById(id): Observable<PolicyResponse>`
- `approveOrReject(id, req: {status, remarks}): Observable<PolicyResponse>`
- `getByStatus(status): Observable<PolicyResponse[]>`
- `getByDisasterType(type): Observable<PolicyResponse[]>`

**Table**: columns: Policy Number, Customer, Agent, Disaster Type, Policy Type, Sum Insured, Premium, Status, Start-End Dates, Actions
**Actions**:
- View Detail: navigate to `/admin/policies/:id`
- Approve/Reject: opens `PolicyApprovalDialog` (only for PENDING/UNDER_REVIEW/FORWARDED policies)

**PolicyApprovalDialog**:
- Title: "Policy Decision — [policyNumber]"
- mat-select Decision: APPROVED / REJECTED
- Textarea Remarks: required
- Sends `PolicyApprovalRequest`: `{ status: string, remarks: string }`
- On save → updates policy in mock array → snackbar

**Filter bar**:
- Status filter: ALL / PENDING / UNDER_REVIEW / FORWARDED / APPROVED / REJECTED / ACTIVE / EXPIRED
- Disaster Type filter: ALL / FLOOD / EARTHQUAKE / CYCLONE / HURRICANE

**Admin Policy Detail (`/admin/policies/:id`)**:
- Full detail card layout (2 columns):
    - Left: Policy info (policyNumber, disasterType, policyType, status chip, remarks)
    - Right: Financial info (propertyValue, sumInsured, premiumAmount, startDate, endDate)
    - Below: Customer info card (userName, userId)
    - Below: Agent info card (agentName, agentId)
    - Below: Zone & Pool info card (disasterZoneName, riskPoolDisasterType)
- Actions: Approve/Reject button (if eligible status)

---

#### 2.1.5 Admin — Disaster Zones (`/admin/disaster-zones`)

**Component**: `AdminDisasterZonesComponent`
**Service**: `AdminDisasterZoneService`
**Methods**: `getAll()`, `create(req)`, `update(id, req)`, `delete(id)`, `getByRiskLevel(level)`, `getByDisasterType(type)`

**Table**: id, Zone Name, Location, Risk Level (chip: HIGH=red, MEDIUM=orange, LOW=green), Disaster Type, Total Policies, Actions
**"Add Zone" button** → `DisasterZoneFormDialog`

**DisasterZoneFormDialog**:
- Zone Name: text input (required)
- Location: text input (required)
- Risk Level: mat-select → LOW / MEDIUM / HIGH / CRITICAL
- Disaster Type: mat-select → FLOOD / EARTHQUAKE / CYCLONE / HURRICANE
- Sends `DisasterZoneRequest`: `{ zoneName, location, riskLevel, disasterType }`

**Filters**: Risk Level dropdown, Disaster Type dropdown

---

#### 2.1.6 Admin — Risk Pools (`/admin/risk-pools`)

**Component**: `AdminRiskPoolsComponent`
**Service**: `AdminRiskPoolService`
**Methods**: `getAll()`, `create(req)`, `update(id, req)`, `delete(id)`, `getCritical()`, `evaluateThreshold(id)`

**Table**: id, Disaster Type, Premium Collected ($), Claims Paid ($), Threshold %, Status (chip), Critical (icon: ⚠ if true), Total Policies, Total Claims, Actions
**"Add Risk Pool" button** → `RiskPoolFormDialog`
**"Evaluate Threshold" button** per row → calls `evaluateThreshold(id)` → updates poolStatus

**RiskPoolFormDialog**:
- Disaster Type: mat-select → FLOOD / EARTHQUAKE / CYCLONE / HURRICANE
- Total Premium Collected: number input
- Total Claims Paid: number input
- Threshold Percentage: number input (0-100, e.g., 75 = critical when claims/premium > 75%)
- Pool Status: mat-select → HEALTHY / WARNING / CRITICAL
- Sends `RiskPoolRequest`: `{ disasterType, totalPremiumCollected, totalClaimsPaid, thresholdPercentage, poolStatus }`

---

#### 2.1.7 Admin — Roles (`/admin/roles`)

**Component**: `AdminRolesComponent`
**Service**: `AdminRoleService`
**Methods**: `getAll()`, `create(req)`, `update(id, req)`, `delete(id)`

**Table**: id, Role Name, Description, Actions
**"Add Role" button** → `RoleFormDialog`

**RoleFormDialog**:
- Name: text input (required, uppercase convention like ADMIN)
- Description: text input (required)
- Sends `RoleRequest`: `{ name, description }`

---

## 2.2 AGENT MODULE — Detailed Breakdown

### Route Prefix: `/agent`
### Guard: `AGENT` role required
### Shell: `AgentShellComponent`

---

#### 2.2.1 Agent Dashboard (`/agent/dashboard`)

**Component**: `AgentDashboardComponent`
**Service**: `AgentDashboardService` → `getDashboard(agentId): Observable<AgentDashboardResponse>`
Agent ID read from localStorage `relief_user_id` (stored as agentId for agent role)

**Layout**:
1. **Welcome Card**: "Welcome back, John Smith" | License: LIC-001 | Region: NORTH
2. **Stat Cards Row 1 (3 across)**: Total Policies Assigned, Pending, Approved
3. **Stat Cards Row 2 (3 across)**: Active Policies, Total Claims on My Policies, Pending Claims
4. **Charts Row (3 across)**:
    - Bar Chart: "Policies by Disaster Type" (policiesByDisasterType map)
    - Bar Chart: "Loss Frequency by Disaster Type" ($) (lossFrequencyByDisasterType map)
    - Donut Chart: "Approval Ratio" (approvalRatio %, rest is unapproved)
5. **Recent Policies Table** (last 3): policyNumber, Customer Name, Disaster Type, Status chip, action button View
6. **Recent Claims Table**: claimNumber, Policy Number, Estimated Loss, Status chip, action button View

---

#### 2.2.2 Agent — My Policies (`/agent/policies`)

**Component**: `AgentPoliciesComponent`
**Service**: `AgentPolicyService`
**Methods**:
- `getMyPolicies(agentId): Observable<PolicyResponse[]>`
- `getById(agentId, policyId): Observable<PolicyResponse>`
- `adjustPremium(agentId, policyId, req: {adjustedPremium, remarks}): Observable<PolicyResponse>`
- `forwardForApproval(agentId, policyId, req: {remarks}): Observable<PolicyResponse>`
- `getByStatus(agentId, status): Observable<PolicyResponse[]>`

**Table**: Policy Number, Customer, Disaster Type, Policy Type, Property Value, Sum Insured, Premium Amount, Status (chip), Actions
**Actions** (conditional on status):
- **Adjust Premium** (all statuses): opens `AdjustPremiumDialog`
    - Adjusted Premium: number input (required, > 0)
    - Remarks: textarea (required)
    - Sends `AgentPremiumAdjustRequest`: `{ adjustedPremium: number, remarks: string }`
    - On save → updates premiumAmount + remarks in mock array → snackbar "Premium updated"
- **Forward for Approval** (only status=PENDING): opens `ForwardPolicyDialog`
    - Remarks/Notes: textarea (required, underwriter notes)
    - Sends `AgentForwardPolicyRequest`: `{ remarks: string }`
    - On save → updates status to "FORWARDED", saves remarks → snackbar "Policy forwarded for admin approval"
- **View Detail**: navigate to `/agent/policies/:id`

**Filter**: Status dropdown (ALL/PENDING/UNDER_REVIEW/FORWARDED/APPROVED/REJECTED/ACTIVE)
**Search**: by policyNumber or customer name

---

#### 2.2.3 Agent — Policy Detail (`/agent/policies/:id`)

Shows all PolicyResponse fields in a structured 2-column card:
- Policy Number (large heading)
- Status chip (prominent)
- Customer: [userName] | [userId]
- Disaster Type | Policy Type
- Property Address
- Property Value | Sum Insured | Premium Amount
- Start Date | End Date
- Remarks (if any)
- Disaster Zone | Risk Pool
- **Action buttons** (same rules as list):
    - Adjust Premium button (if eligible)
    - Forward for Approval button (if PENDING)

---

#### 2.2.4 Agent — Claims on My Policies (`/agent/claims`)

**Component**: `AgentClaimsComponent`
**Service**: `AgentClaimsService`
Method: `getClaimsOnMyPolicies(agentId): Observable<ClaimResponse[]>`

**Table**: Claim Number, Policy Number, Description (truncated 50 chars), Estimated Loss, Approved Amount, Status (chip), Filed Date, Resolved Date
**Read only** — no modify actions
**Filter**: by status
**View detail**: navigate to `/agent/policies/:policyId` (cross-link to the related policy) OR show an expandable row with full claim detail

---

## 2.3 CLAIMS OFFICER MODULE — Detailed Breakdown

### Route Prefix: `/claims-officer`
### Guard: `CLAIMS_OFFICER` role required
### Shell: `ClaimsOfficerShellComponent`

---

#### 2.3.1 Claims Officer Dashboard (`/claims-officer/dashboard`)

**Component**: `ClaimsOfficerDashboardComponent`
**Service**: `ClaimsOfficerDashboardService` → `getDashboard(): Observable<ClaimsOfficerDashboardResponse>`

**Layout**:
1. **Stat Cards Row 1 (4 across)**: Total Claims, Filed Claims, Under Review, Approved Claims
2. **Stat Cards Row 2 (4 across)**: Rejected Claims, Paid Claims, Total Approved Amount ($), Total Paid Amount ($)
3. **High Value Claims count** (special highlighted banner): "[X] claims exceed the high-value threshold"
4. **Charts Row (2 side by side)**:
    - Pie Chart: "Claims by Disaster Type"
    - Bar Chart: "Claims by Status"
5. **Risk Pool Snapshot Table**:
    - Columns: Disaster Type, Premium Collected, Claims Paid, Status chip, Critical (⚠ icon)
6. **Pending Attention Claims** (card list, compact):
    - Shows claims with status FILED or SURVEY_ASSIGNED
    - Each item: claim number, policy number, estimated loss ($), status chip, filed date, "Review" button → navigates to `/claims-officer/claims/:id`

---

#### 2.3.2 Claims Officer — All Claims (`/claims-officer/claims`)

**Component**: `ClaimsOfficerClaimsComponent`
**Service**: `ClaimsOfficerClaimsService`
**Methods**:
- `getAll(): Observable<ClaimResponse[]>`
- `getById(id): Observable<ClaimResponse>`
- `getByStatus(status): Observable<ClaimResponse[]>`
- `getByDisasterType(type): Observable<ClaimResponse[]>`
- `markUnderReview(id): Observable<ClaimResponse>`
- `decideClaim(id, req: {decision, approvedAmount, remarks}): Observable<ClaimResponse>`
- `getHighValue(threshold): Observable<ClaimResponse[]>`

**Table**: Claim Number, Policy Number, Description (truncated), Estimated Loss, Approved Amount, Status (chip), Filed Date, Resolved Date, Actions

**Actions** (conditional):
- **Mark Under Review** (status=FILED or SURVEY_ASSIGNED): button → no dialog, direct action → calls `markUnderReview(id)` → updates status to UNDER_REVIEW in mock → snackbar "Claim marked Under Review"
- **Decide on Claim** (status=UNDER_REVIEW): opens `ClaimDecisionDialog`
    - Decision: mat-radio-group → APPROVED / REJECTED
    - Approved Amount: number input (required only if APPROVED, disabled if REJECTED)
    - Remarks: textarea (required)
    - Sends `ClaimDecisionRequest`: `{ decision: string, approvedAmount: number, remarks: string }`
    - On save → updates claim in mock array (status=APPROVED or REJECTED, approvedAmount, officerRemarks, resolvedDate=now) → snackbar
- **View Detail**: navigate to `/claims-officer/claims/:id`

**Filters**: Status dropdown (ALL/FILED/SURVEY_ASSIGNED/UNDER_REVIEW/APPROVED/REJECTED/PAID), Disaster Type dropdown

---

#### 2.3.3 Claims Officer — Claim Detail (`/claims-officer/claims/:id`)

**Layout**:
1. **Claim Info Card** (left column):
    - Claim Number (large), Status chip, Filed Date, Resolved Date
    - Description (full text)
    - Estimated Loss, Approved Amount (show if set)
    - Officer Remarks (show if set)
2. **Linked Policy Card** (right column):
    - Policy Number, Disaster Type, Property Address
    - Sum Insured, Premium Amount
    - Customer Name
3. **Action buttons** at bottom:
    - Mark Under Review (if FILED/SURVEY_ASSIGNED)
    - Decide on Claim (if UNDER_REVIEW) → opens same `ClaimDecisionDialog`

---

#### 2.3.4 Claims Officer — High-Value Claims (`/claims-officer/high-value`)

**Component**: `HighValueClaimsComponent`
**Service**: reuses `ClaimsOfficerClaimsService.getHighValue(threshold)`

**Top**: "High-Value Claims" title + threshold input (mat-form-field, type=number, default=50000) with "Apply" button → filters list dynamically
**Same table + actions as All Claims**
**Info banner**: "Showing claims with estimated loss > $[threshold]"

---

## 2.4 CUSTOMER MODULE — Detailed Breakdown

### Route Prefix: `/customer`
### Guard: `CUSTOMER` role required
### Shell: `CustomerShellComponent`

---

#### 2.4.1 Customer Dashboard (`/customer/dashboard`)

**Component**: `CustomerDashboardComponent`
**Service**: `CustomerDashboardService`
Method: `getDashboard(userId): Observable<UserDashboardResponse>`
User ID read from `localStorage.getItem('relief_user_id')`

**Layout**:
1. **Welcome Banner**: "Hello, [name]! 👋" subtitle: "Here's your insurance overview"
2. **Stat Cards Row 1 (3 across)**: Total Policies, Active Policies, Pending Policies
3. **Stat Cards Row 2 (3 across)**: Total Claims, Pending Claims, Total Premium Paid ($)
4. **Quick Actions Row**: 3 action cards with icons:
    - "Apply for Policy" (add_circle icon) → button → navigate to `/customer/policies/new`
    - "File a Claim" (report icon) → button → navigate to `/customer/claims/new`
    - "Pay Premium" (payment icon) → button → navigate to `/customer/policies`
5. **Recent Policies table** (compact, 5 rows max):
    - policyNumber, disasterType, status chip, action "View" → `/customer/policies/:id`
6. **Recent Claims table** (compact, 5 rows max):
    - claimNumber, policyNumber, estimatedLoss, status chip, action "View"
7. **Recent Payments table** (compact, 5 rows max):
    - policyNumber, amount, paymentStatus chip, paymentDate

---

#### 2.4.2 Customer — My Policies (`/customer/policies`)

**Component**: `CustomerPoliciesComponent`
**Service**: `CustomerPolicyService`
**Methods**:
- `getMyPolicies(userId): Observable<PolicyResponse[]>`
- `getById(userId, policyId): Observable<PolicyResponse>`
- `submitPolicy(userId, req: UserPolicyRequest): Observable<PolicyResponse>`
- `getByStatus(userId, status): Observable<PolicyResponse[]>`

**Table**: Policy Number, Disaster Type, Policy Type, Property Address, Sum Insured, Premium Amount, Status (chip), Start Date, End Date, Actions
**Actions**:
- **Pay Premium** (only if status=APPROVED): button → opens `PayPremiumDialog`
    - Shows: Policy Number, Premium Amount
    - Confirmation text: "Pay $[premiumAmount] for policy [policyNumber]?"
    - Sends `PremiumPaymentRequest`: `{ policyId: number }`
    - On confirm → adds new `PaymentResponse` to customer payment service mock array, updates policy status to ACTIVE → snackbar "Premium paid successfully! Policy is now ACTIVE"
- **View Detail**: navigate to `/customer/policies/:id`
  **Filter**: Status dropdown

**"Apply for New Policy" button** (top right) → navigate to `/customer/policies/new`

---

#### 2.4.3 Customer — Apply for Policy (`/customer/policies/new`)

**Component**: `CustomerNewPolicyComponent`
**Form**: Reactive form with `UserPolicyRequest` shape

**Form Fields** (all required unless noted):
1. **Disaster Type** — `mat-select`: FLOOD, EARTHQUAKE, CYCLONE, HURRICANE
2. **Policy Type** — `mat-select`: BASIC ($), STANDARD ($$), PREMIUM ($$$) — show estimated premium hint per type
3. **Property Address** — `mat-textarea` (required, min 10 chars)
4. **Property Value** — `mat-input` type=number (required, > 0)
5. **Sum Insured** — `mat-input` type=number (required, > 0)
    - Validator: sumInsured <= propertyValue (custom validator)
    - Hint: "Cannot exceed property value"
6. **Start Date** — `mat-datepicker` (required, today or future)
7. **End Date** — `mat-datepicker` (required, > startDate)

**Premium Preview**: computed field below the form (readonly mat-input):
- BASIC = sumInsured * 0.01
- STANDARD = sumInsured * 0.025
- PREMIUM = sumInsured * 0.04
- Updates reactively as policyType/sumInsured changes using `valueChanges`

**Buttons**: Cancel (navigate back), Submit Application

**On submit**: adds new `PolicyResponse` to service mock array with:
- id: auto-increment
- policyNumber: "POL-NEW-[timestamp]"
- userId: from localStorage
- userName: from localStorage
- agentId: 1 (default agent)
- agentName: "John Smith"
- status: "PENDING"
- premiumAmount: computed above
- all other fields from form

→ snackbar "Policy application submitted! Status: PENDING" → navigate to `/customer/policies`

---

#### 2.4.4 Customer — Policy Detail (`/customer/policies/:id`)

**Layout**:
1. **Policy Header**: Policy Number (large), Status chip (large)
2. **Info Cards (2 columns)**:
    - **Coverage Details**: Disaster Type, Policy Type, Property Address
    - **Financial Details**: Property Value, Sum Insured, Premium Amount
3. **Timeline**: Start Date → End Date (visual date range)
4. **Remarks** (if any): styled info box
5. **Assigned Agent card**: agentName
6. **Risk Info**: disasterZoneName, riskPoolDisasterType
7. **Action Button** (if status=APPROVED): "Pay Premium — $[premiumAmount]" → opens PayPremiumDialog

---

#### 2.4.5 Customer — My Claims (`/customer/claims`)

**Component**: `CustomerClaimsComponent`
**Service**: `CustomerClaimsService`
**Methods**:
- `getMyClaims(userId): Observable<ClaimResponse[]>`
- `getById(userId, claimId): Observable<ClaimResponse>`
- `fileClaim(userId, req: {policyId, description, estimatedLoss}): Observable<ClaimResponse>`

**Table**: Claim Number, Policy Number, Description (truncated), Estimated Loss, Approved Amount, Status (chip), Officer Remarks, Filed Date, Resolved Date
**Actions**: View Detail only (expand row or navigate)
**Filter**: Status dropdown

**"File New Claim" button** (top right) → navigate to `/customer/claims/new`

---

#### 2.4.6 Customer — File a Claim (`/customer/claims/new`)

**Component**: `CustomerNewClaimComponent`
**Form**: Reactive form with `ClaimRequest` shape

**Form Fields** (all required):
1. **Select Policy** — `mat-select` showing user's ACTIVE or APPROVED policies:
    - Option format: "POL-XXX — FLOOD — STANDARD — $[sumInsured]"
    - Value: policyId (number)
2. **Claim Description** — `mat-textarea` (required, minLength 20 chars):
    - Placeholder: "Describe the disaster event, extent of damage, affected areas..."
    - Character counter shown (matTextareaAutosize)
3. **Estimated Loss** — `mat-input` type=number:
    - Required, must be > 0
    - Must not exceed sumInsured of selected policy (cross-field validation)
    - Hint: "Estimated damage value in USD"

**Info Banner**: "Claims are processed within 7-10 business days. Ensure your policy is active before filing."

**Buttons**: Cancel, Submit Claim

**On submit**: adds new `ClaimResponse` to service mock array with:
- id: auto-increment
- claimNumber: "CLM-NEW-[timestamp]"
- policyId: from form
- policyNumber: looked up from policies mock
- description: from form
- estimatedLoss: from form
- approvedAmount: null
- status: "FILED"
- officerRemarks: null
- filedDate: new Date().toISOString()
- resolvedDate: null

→ snackbar "Claim filed successfully! Claim Number: [claimNumber]" → navigate to `/customer/claims`

---

#### 2.4.7 Customer — My Payments (`/customer/payments`)

**Component**: `CustomerPaymentsComponent`
**Service**: `CustomerPaymentService`
**Methods**:
- `getMyPayments(userId): Observable<PaymentResponse[]>`
- `payPremium(userId, req: {policyId}): Observable<PaymentResponse>`

**Table**: #, Policy Number, Payment Type, Amount ($), Status (chip), Payment Date

**Empty state**: If no payments, show illustration + "No payments yet. Pay your first premium to activate a policy." + button "Go to My Policies"

**Filter**: by paymentStatus (ALL / PENDING / COMPLETED / FAILED)

---

## 2.5 AUTH MODULE — Detailed Breakdown

### Staff Login (`/staff-login`)
**Component**: `StaffLoginComponent`
**Form**:
```typescript
loginForm = fb.group({
  email: ['', [Validators.required, Validators.email]],
  password: ['', [Validators.required, Validators.minLength(6)]]
});
```
**Mock Auth Logic** (in `AuthService`):
```typescript
const STAFF_USERS = [
  { email: 'admin@relief.com', password: 'admin123', role: 'ADMIN', userId: 1, name: 'Admin User' },
  { email: 'john@relief.com', password: 'agent123', role: 'AGENT', userId: 2, name: 'John Smith' },
  { email: 'sara@relief.com', password: 'officer123', role: 'CLAIMS_OFFICER', userId: 5, name: 'Sara Davis' }
];
```
On match: localStorage.setItem('relief_token', 'mock.jwt.token'), setItem('relief_role', user.role), setItem('relief_user_id', user.userId), setItem('relief_name', user.name)
On no match: show error "Invalid email or password"

**UI Elements**:
- Full-height split layout: left side = navy branding panel with logo, tagline "Protecting communities when it matters most", right side = login form on white card
- "Staff Portal" badge above form
- Link at bottom: "Are you a customer? Login here →" → `/customer-login`

### Customer Login (`/customer-login`)
**Same form structure** but different mock users:
```typescript
const CUSTOMER_USERS = [
  { email: 'alice@relief.com', password: 'customer123', role: 'CUSTOMER', userId: 3, name: 'Alice Johnson' },
  { email: 'bob@relief.com', password: 'customer123', role: 'CUSTOMER', userId: 4, name: 'Bob Williams' }
];
```
Route on success: `/customer/dashboard`
**UI**: Similar split layout but with softer colors (light blue/teal left panel)
- "Customer Portal" badge
- Register link: "New here? Create account →" → `/register`
- Link: "Staff login →" → `/staff-login`

### Register (`/register`)
**Form**:
```typescript
registerForm = fb.group({
  name: ['', [Validators.required, Validators.minLength(2)]],
  email: ['', [Validators.required, Validators.email]],
  password: ['', [Validators.required, Validators.minLength(6)]],
  confirmPassword: ['', Validators.required]
}, { validators: passwordMatchValidator });
```
On submit: mock adds to CUSTOMER_USERS in AuthService → navigate to `/customer-login` with query param `?registered=true` → customer-login shows "Registration successful! Please login."

---

*End of Part 2*

---
---
---

# ═══════════════════════════════════════════════════════════════
# PART 3 — COMPLETE FILE STRUCTURE
# ═══════════════════════════════════════════════════════════════

```
src/
└── app/
    ├── app.config.ts                          # provideRouter, provideAnimations, provideHttpClient
    ├── app.routes.ts                          # all lazy-loaded routes
    │
    ├── core/
    │   ├── guards/
    │   │   ├── auth.guard.ts                  # checks relief_token in localStorage
    │   │   └── role.guard.ts                  # checks relief_role matches route data.role
    │   ├── models/
    │   │   ├── policy.model.ts                # PolicyResponse interface
    │   │   ├── claim.model.ts                 # ClaimResponse interface
    │   │   ├── payment.model.ts               # PaymentResponse interface
    │   │   ├── user.model.ts                  # UserResponse interface
    │   │   ├── agent.model.ts                 # AgentResponse interface
    │   │   ├── disaster-zone.model.ts         # DisasterZoneResponse interface
    │   │   ├── risk-pool.model.ts             # RiskPoolResponse interface
    │   │   └── role.model.ts                  # RoleResponse interface
    │   └── services/
    │       └── auth.service.ts                # login/logout/register, localStorage management
    │
    ├── shared/
    │   ├── components/
    │   │   ├── stat-card/
    │   │   │   ├── stat-card.component.ts     # @Input title, value, icon, color
    │   │   │   └── stat-card.component.html
    │   │   ├── status-badge/
    │   │   │   ├── status-badge.component.ts  # @Input status → colored mat-chip
    │   │   │   └── status-badge.component.html
    │   │   ├── confirm-dialog/
    │   │   │   ├── confirm-dialog.component.ts
    │   │   │   └── confirm-dialog.component.html
    │   │   └── app-shell/
    │   │       ├── app-shell.component.ts     # shared sidenav + toolbar wrapper
    │   │       └── app-shell.component.html
    │   └── pipes/
    │       └── currency-format.pipe.ts        # formats numbers as $1,000.00
    │
    └── features/
        │
        ├── auth/
        │   ├── staff-login/
        │   │   └── staff-login.component.ts
        │   ├── customer-login/
        │   │   └── customer-login.component.ts
        │   └── register/
        │       └── register.component.ts
        │
        ├── admin/
        │   ├── shell/
        │   │   └── admin-shell.component.ts          # injects AppShellComponent with admin nav items
        │   ├── dashboard/
        │   │   ├── components/
        │   │   │   └── admin-dashboard.component.ts
        │   │   └── services/
        │   │       └── admin-dashboard.service.ts    # mock AdminDashboardResponse
        │   ├── users/
        │   │   ├── components/
        │   │   │   ├── admin-users.component.ts      # main list
        │   │   │   ├── admin-user-detail.component.ts
        │   │   │   ├── user-status-dialog.component.ts
        │   │   │   └── assign-role-dialog.component.ts
        │   │   └── services/
        │   │       └── admin-user.service.ts         # mock users array + CRUD methods
        │   ├── agents/
        │   │   ├── components/
        │   │   │   ├── admin-agents.component.ts
        │   │   │   └── agent-form-dialog.component.ts
        │   │   └── services/
        │   │       └── admin-agent.service.ts        # mock agents array + CRUD
        │   ├── policies/
        │   │   ├── components/
        │   │   │   ├── admin-policies.component.ts
        │   │   │   ├── admin-policy-detail.component.ts
        │   │   │   └── policy-approval-dialog.component.ts
        │   │   └── services/
        │   │       └── admin-policy.service.ts       # mock policies array (4 policies)
        │   ├── disaster-zones/
        │   │   ├── components/
        │   │   │   ├── admin-disaster-zones.component.ts
        │   │   │   └── disaster-zone-form-dialog.component.ts
        │   │   └── services/
        │   │       └── admin-disaster-zone.service.ts # mock 3 zones
        │   ├── risk-pools/
        │   │   ├── components/
        │   │   │   ├── admin-risk-pools.component.ts
        │   │   │   └── risk-pool-form-dialog.component.ts
        │   │   └── services/
        │   │       └── admin-risk-pool.service.ts    # mock 3 pools
        │   └── roles/
        │       ├── components/
        │       │   ├── admin-roles.component.ts
        │       │   └── role-form-dialog.component.ts
        │       └── services/
        │           └── admin-role.service.ts         # mock 4 roles
        │
        ├── agent/
        │   ├── shell/
        │   │   └── agent-shell.component.ts
        │   ├── dashboard/
        │   │   ├── components/
        │   │   │   └── agent-dashboard.component.ts
        │   │   └── services/
        │   │       └── agent-dashboard.service.ts    # mock AgentDashboardResponse
        │   ├── policies/
        │   │   ├── components/
        │   │   │   ├── agent-policies.component.ts
        │   │   │   ├── agent-policy-detail.component.ts
        │   │   │   ├── adjust-premium-dialog.component.ts
        │   │   │   └── forward-policy-dialog.component.ts
        │   │   └── services/
        │   │       └── agent-policy.service.ts       # mock 4 policies (same data as admin)
        │   └── claims/
        │       ├── components/
        │       │   └── agent-claims.component.ts
        │       └── services/
        │           └── agent-claims.service.ts       # mock claims on agent's policies
        │
        ├── claimsOfficer/
        │   ├── shell/
        │   │   └── claims-officer-shell.component.ts
        │   ├── dashboard/
        │   │   ├── components/
        │   │   │   └── claims-officer-dashboard.component.ts
        │   │   └── services/
        │   │       └── claims-officer-dashboard.service.ts # mock ClaimsOfficerDashboardResponse
        │   ├── claims/
        │   │   ├── components/
        │   │   │   ├── claims-officer-claims.component.ts
        │   │   │   ├── claims-officer-claim-detail.component.ts
        │   │   │   └── claim-decision-dialog.component.ts
        │   │   └── services/
        │   │       └── claims-officer-claims.service.ts # mock 1 claim (CLM-BOB-001)
        │   └── high-value/
        │       ├── components/
        │       │   └── high-value-claims.component.ts
        │       └── services/
        │           └── high-value-claims.service.ts  # delegates to ClaimsOfficerClaimsService
        │
        └── customer/
            ├── shell/
            │   └── customer-shell.component.ts
            ├── dashboard/
            │   ├── components/
            │   │   └── customer-dashboard.component.ts
            │   └── services/
            │       └── customer-dashboard.service.ts  # mock UserDashboardResponse (Alice+Bob)
            ├── policies/
            │   ├── components/
            │   │   ├── customer-policies.component.ts
            │   │   ├── customer-policy-detail.component.ts
            │   │   ├── customer-new-policy.component.ts
            │   │   └── pay-premium-dialog.component.ts
            │   └── services/
            │       └── customer-policy.service.ts     # mock policies per user
            ├── claims/
            │   ├── components/
            │   │   ├── customer-claims.component.ts
            │   │   ├── customer-new-claim.component.ts
            │   │   └── customer-claim-detail.component.ts
            │   └── services/
            │       └── customer-claims.service.ts     # mock claims per user
            └── payments/
                ├── components/
                │   └── customer-payments.component.ts
                └── services/
                    └── customer-payment.service.ts    # mock payments per user (initially empty, built up by Pay Premium)
```

---

## Key Interface Definitions (core/models)

### policy.model.ts
```typescript
export interface PolicyResponse {
  id: number;
  policyNumber: string;
  userId: number;
  userName: string;
  agentId: number;
  agentName: string;
  disasterType: string;        // FLOOD | EARTHQUAKE | CYCLONE | HURRICANE
  policyType: string;          // BASIC | STANDARD | PREMIUM
  propertyAddress: string;
  propertyValue: number;
  sumInsured: number;
  premiumAmount: number;
  status: string;              // PENDING | UNDER_REVIEW | FORWARDED | APPROVED | REJECTED | ACTIVE | EXPIRED
  remarks: string | null;
  startDate: string;           // ISO date string YYYY-MM-DD
  endDate: string;
  disasterZoneName: string;
  riskPoolDisasterType: string;
}

export interface UserPolicyRequest {
  disasterType: string;
  policyType: string;
  propertyAddress: string;
  propertyValue: number;
  sumInsured: number;
  startDate: string;
  endDate: string;
}
```

### claim.model.ts
```typescript
export interface ClaimResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  description: string;
  estimatedLoss: number;
  approvedAmount: number | null;
  status: string;              // FILED | SURVEY_ASSIGNED | UNDER_REVIEW | APPROVED | REJECTED | PAID
  officerRemarks: string | null;
  filedDate: string;           // ISO datetime string
  resolvedDate: string | null;
}

export interface ClaimRequest {
  policyId: number;
  description: string;
  estimatedLoss: number;
}

export interface ClaimDecisionRequest {
  decision: string;            // APPROVED | REJECTED
  approvedAmount: number;
  remarks: string;
}
```

### payment.model.ts
```typescript
export interface PaymentResponse {
  id: number;
  policyId: number;
  policyNumber: string;
  claimId: number | null;
  paymentType: string;         // PREMIUM | CLAIM_PAYOUT
  amount: number;
  paymentStatus: string;       // PENDING | COMPLETED | FAILED
  paymentDate: string;         // ISO datetime
}

export interface PremiumPaymentRequest {
  policyId: number;
}
```

### user.model.ts
```typescript
export interface UserResponse {
  id: number;
  name: string;
  email: string;
  status: string;              // ACTIVE | INACTIVE
  createdAt: string;           // ISO datetime
  roles: string[];             // e.g., ['ADMIN'] or ['CUSTOMER']
}

export interface UserStatusRequest {
  status: string;
}

export interface AssignRoleRequest {
  userId: number;
  roleNames: string[];
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}
```

### agent.model.ts
```typescript
export interface AgentResponse {
  id: number;
  userId: number;
  userName: string;
  userEmail: string;
  licenseNumber: string;
  region: string;              // NORTH | SOUTH | EAST | WEST | CENTRAL
  totalPolicies: number;
}

export interface AgentRequest {
  userId: number;
  licenseNumber: string;
  region: string;
}

export interface AgentPremiumAdjustRequest {
  adjustedPremium: number;
  remarks: string;
}

export interface AgentForwardPolicyRequest {
  remarks: string;
}
```

### disaster-zone.model.ts
```typescript
export interface DisasterZoneResponse {
  id: number;
  zoneName: string;
  location: string;
  riskLevel: string;           // LOW | MEDIUM | HIGH | CRITICAL
  disasterType: string;
  totalPolicies: number;
}

export interface DisasterZoneRequest {
  zoneName: string;
  location: string;
  riskLevel: string;
  disasterType: string;
}
```

### risk-pool.model.ts
```typescript
export interface RiskPoolResponse {
  id: number;
  disasterType: string;
  totalPremiumCollected: number;
  totalClaimsPaid: number;
  thresholdPercentage: number;
  poolStatus: string;          // HEALTHY | WARNING | CRITICAL
  criticalFlag: boolean;
  totalPolicies: number;
  totalClaims: number;
}

export interface RiskPoolRequest {
  disasterType: string;
  totalPremiumCollected: number;
  totalClaimsPaid: number;
  thresholdPercentage: number;
  poolStatus: string;
}
```

### role.model.ts
```typescript
export interface RoleResponse {
  id: number;
  name: string;
  description: string;
}

export interface RoleRequest {
  name: string;
  description: string;
}
```

---

## Service Pattern (Every Service Must Follow This)

```typescript
@Injectable({ providedIn: 'root' })
export class SomeFeatureService {

  // All mock data defined HERE — never in component or HTML
  private mockData: SomeModel[] = [
    { id: 1, field: 'value', ... },
    { id: 2, field: 'value', ... }
  ];

  getAll(): Observable<SomeModel[]> {
    return of([...this.mockData]).pipe(delay(300));
  }

  getById(id: number): Observable<SomeModel> {
    const item = this.mockData.find(x => x.id === id)!;
    return of({ ...item }).pipe(delay(300));
  }

  create(req: SomeRequest): Observable<SomeModel> {
    const newItem: SomeModel = {
      id: Math.max(...this.mockData.map(x => x.id), 0) + 1,
      ...req,
      // defaults
    };
    this.mockData.push(newItem);
    return of({ ...newItem }).pipe(delay(300));
  }

  update(id: number, req: SomeRequest): Observable<SomeModel> {
    const index = this.mockData.findIndex(x => x.id === id);
    this.mockData[index] = { ...this.mockData[index], ...req };
    return of({ ...this.mockData[index] }).pipe(delay(300));
  }

  delete(id: number): Observable<void> {
    this.mockData = this.mockData.filter(x => x.id !== id);
    return of(undefined).pipe(delay(300));
  }
}
```

---

## Auth Service Pattern

```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly STAFF_USERS = [
    { email: 'admin@relief.com', password: 'admin123', role: 'ADMIN', userId: 1, name: 'Admin User' },
    { email: 'john@relief.com', password: 'agent123', role: 'AGENT', userId: 2, name: 'John Smith' },
    { email: 'sara@relief.com', password: 'officer123', role: 'CLAIMS_OFFICER', userId: 5, name: 'Sara Davis' }
  ];

  private readonly CUSTOMER_USERS = [
    { email: 'alice@relief.com', password: 'customer123', role: 'CUSTOMER', userId: 3, name: 'Alice Johnson' },
    { email: 'bob@relief.com', password: 'customer123', role: 'CUSTOMER', userId: 4, name: 'Bob Williams' }
  ];

  loginStaff(email: string, password: string): Observable<boolean> {
    const user = this.STAFF_USERS.find(u => u.email === email && u.password === password);
    if (user) {
      localStorage.setItem('relief_token', 'mock.jwt.' + btoa(user.email));
      localStorage.setItem('relief_role', user.role);
      localStorage.setItem('relief_user_id', String(user.userId));
      localStorage.setItem('relief_name', user.name);
      return of(true).pipe(delay(300));
    }
    return throwError(() => new Error('Invalid email or password')).pipe(delay(300));
  }

  loginCustomer(email: string, password: string): Observable<boolean> {
    const user = this.CUSTOMER_USERS.find(u => u.email === email && u.password === password);
    if (user) {
      localStorage.setItem('relief_token', 'mock.jwt.' + btoa(user.email));
      localStorage.setItem('relief_role', user.role);
      localStorage.setItem('relief_user_id', String(user.userId));
      localStorage.setItem('relief_name', user.name);
      return of(true).pipe(delay(300));
    }
    return throwError(() => new Error('Invalid email or password')).pipe(delay(300));
  }

  logout(): void {
    localStorage.removeItem('relief_token');
    localStorage.removeItem('relief_role');
    localStorage.removeItem('relief_user_id');
    localStorage.removeItem('relief_name');
  }

  getRole(): string | null { return localStorage.getItem('relief_role'); }
  getUserId(): number { return Number(localStorage.getItem('relief_user_id')); }
  getName(): string { return localStorage.getItem('relief_name') || ''; }
  isLoggedIn(): boolean { return !!localStorage.getItem('relief_token'); }
}
```

---

## AppShell Component Pattern

```typescript
// app-shell.component.ts
@Component({
  selector: 'app-shell',
  template: `
    <mat-sidenav-container class="shell-container">
      <mat-sidenav #sidenav [mode]="isMobile ? 'over' : 'side'" [opened]="!isMobile" class="sidenav">
        <div class="logo-area">
          <mat-icon>shield</mat-icon>
          <span>Relief</span>
        </div>
        <mat-nav-list>
          <a mat-list-item *ngFor="let item of navItems" [routerLink]="item.route" routerLinkActive="active-link">
            <mat-icon matListItemIcon>{{item.icon}}</mat-icon>
            <span matListItemTitle>{{item.label}}</span>
          </a>
        </mat-nav-list>
      </mat-sidenav>
      <mat-sidenav-content>
        <mat-toolbar color="primary">
          <button mat-icon-button (click)="sidenav.toggle()"><mat-icon>menu</mat-icon></button>
          <span>{{pageTitle}}</span>
          <span class="spacer"></span>
          <span class="role-badge" [class]="'role-' + role.toLowerCase()">{{role}}</span>
          <span class="user-name">{{userName}}</span>
          <button mat-icon-button (click)="logout()"><mat-icon>logout</mat-icon></button>
        </mat-toolbar>
        <div class="content-area">
          <router-outlet></router-outlet>
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `
})
export class AppShellComponent {
  @Input() navItems: NavItem[];   // passed by each role shell
  role = authService.getRole();
  userName = authService.getName();
}
```

---

## Summary — Quick Reference

| Feature | Route | Service | Key DTOs Used |
|---|---|---|---|
| Staff Login | /staff-login | AuthService.loginStaff() | JwtRequest (email+password) |
| Customer Login | /customer-login | AuthService.loginCustomer() | JwtRequest |
| Register | /register | AuthService.register() | RegisterRequest |
| Admin Dashboard | /admin/dashboard | AdminDashboardService | AdminDashboardResponse |
| Admin Users | /admin/users | AdminUserService | UserResponse, AssignRoleRequest, UserStatusRequest |
| Admin Agents | /admin/agents | AdminAgentService | AgentResponse, AgentRequest |
| Admin Policies | /admin/policies | AdminPolicyService | PolicyResponse, PolicyApprovalRequest |
| Admin Disaster Zones | /admin/disaster-zones | AdminDisasterZoneService | DisasterZoneResponse, DisasterZoneRequest |
| Admin Risk Pools | /admin/risk-pools | AdminRiskPoolService | RiskPoolResponse, RiskPoolRequest |
| Admin Roles | /admin/roles | AdminRoleService | RoleResponse, RoleRequest |
| Agent Dashboard | /agent/dashboard | AgentDashboardService | AgentDashboardResponse |
| Agent Policies | /agent/policies | AgentPolicyService | PolicyResponse, AgentPremiumAdjustRequest, AgentForwardPolicyRequest |
| Agent Claims | /agent/claims | AgentClaimsService | ClaimResponse |
| CO Dashboard | /claims-officer/dashboard | ClaimsOfficerDashboardService | ClaimsOfficerDashboardResponse |
| CO All Claims | /claims-officer/claims | ClaimsOfficerClaimsService | ClaimResponse, ClaimDecisionRequest |
| CO High-Value | /claims-officer/high-value | ClaimsOfficerClaimsService | ClaimResponse |
| Customer Dashboard | /customer/dashboard | CustomerDashboardService | UserDashboardResponse |
| Customer Policies | /customer/policies | CustomerPolicyService | PolicyResponse, UserPolicyRequest |
| Customer New Policy | /customer/policies/new | CustomerPolicyService | UserPolicyRequest |
| Customer Claims | /customer/claims | CustomerClaimsService | ClaimResponse, ClaimRequest |
| Customer New Claim | /customer/claims/new | CustomerClaimsService | ClaimRequest |
| Customer Payments | /customer/payments | CustomerPaymentService | PaymentResponse, PremiumPaymentRequest |

---

*End of Part 3*
