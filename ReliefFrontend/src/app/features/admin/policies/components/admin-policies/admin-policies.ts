import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminPolicyService, AssignAgentRequest } from '../../services/admin-policy';
import { AdminAgentService } from '../../../agents/services/admin-agent';
import { PolicyResponse } from '../../../../../core/models/policy.model';
import { AgentResponse } from '../../../../../core/models/agent.model';
import { RequestStateService } from '../../../../../core/services/request-state.service';
@Component({
  selector: 'app-admin-policies',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-policies.html',
  styleUrl: './admin-policies.css',
})
export class AdminPolicies implements OnInit {
  private svc = inject(AdminPolicyService);
  private agentSvc = inject(AdminAgentService);
  private router = inject(Router);
  requestState = inject(RequestStateService);
  policies = signal<PolicyResponse[]>([]);
  filtered = signal<PolicyResponse[]>([]);
  filterStatus = 'ALL';
  filterDisaster = 'ALL';
  showAssignAgentDialog = signal(false);
  selectedPolicy = signal<PolicyResponse | null>(null);
  selectedAgentId: number | null = null;
  dialogAgents = signal<AgentResponse[]>([]);
  loadingDialogAgents = signal(false);
  page = signal(0);
  pageSize = 10;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.svc.getAll().subscribe(data => { this.policies.set(data); this.applyFilters(); });
  }

  applyFilters(): void {
    let result = [...this.policies()];
    if (this.filterStatus !== 'ALL') result = result.filter(p => p.status === this.filterStatus);
    if (this.filterDisaster !== 'ALL') result = result.filter(p => p.disasterType === this.filterDisaster);
    this.filtered.set(result);
    this.page.set(0);
  }

  get paginated(): PolicyResponse[] {
    return this.filtered().slice(this.page() * this.pageSize, (this.page() + 1) * this.pageSize);
  }

  get totalPages(): number { return Math.ceil(this.filtered().length / this.pageSize); }

  openAssignAgent(policy: PolicyResponse): void {
    if (policy.agentId) return;
    this.selectedPolicy.set(policy);
    this.selectedAgentId = null;
    this.dialogAgents.set([]);
    this.loadingDialogAgents.set(true);
    this.showAssignAgentDialog.set(true);
    const fetch$ = policy.region
      ? this.agentSvc.getByRegion(policy.region)
      : this.agentSvc.getAll();
    fetch$.subscribe(agents => {
      this.dialogAgents.set(agents);
      this.loadingDialogAgents.set(false);
    });
  }

  confirmAssignAgent(): void {
    if (!this.selectedPolicy() || !this.selectedAgentId) return;
    const req: AssignAgentRequest = { agentId: this.selectedAgentId };
    this.svc.assignAgent(this.selectedPolicy()!.id, req).subscribe(() => {
      this.showAssignAgentDialog.set(false);
      this.selectedPolicy.set(null);
      this.selectedAgentId = null;
      this.load();
    });
  }

  viewDetail(id: number): void {
    this.router.navigate(['/admin/policies', id]);
  }

  statusClass(status: string): string {
    const map: Record<string, string> = {
      ACTIVE: 'bg-green-100 text-green-700',
      APPROVED: 'bg-green-100 text-green-700',
      PENDING: 'bg-amber-100 text-amber-700',
      UNDER_REVIEW: 'bg-yellow-100 text-yellow-700',
      FORWARDED: 'bg-[#F3F4F4] text-[#612D53]',
      REJECTED: 'bg-red-100 text-red-700',
      EXPIRED: 'bg-gray-100 text-gray-500',
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  disasterClass(type: string): string {
    const map: Record<string, string> = {
      FLOOD: 'bg-[#F3F4F4] text-[#612D53]',
      EARTHQUAKE: 'bg-orange-100 text-orange-700',
      CYCLONE: 'bg-purple-100 text-purple-700',
      HURRICANE: 'bg-red-100 text-red-700',
      WILDFIRE: 'bg-amber-100 text-amber-700',
      LANDSLIDE: 'bg-stone-100 text-stone-700',
    };
    return map[type] || 'bg-gray-100 text-gray-600';
  }

  formatCurrency(v: number): string { return '₹' + v.toLocaleString(); }
}
