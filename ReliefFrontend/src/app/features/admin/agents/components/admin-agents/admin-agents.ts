import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminAgentService } from '../../services/admin-agent';
import { AgentResponse } from '../../../../../core/models/agent.model';

@Component({
  selector: 'app-admin-agents',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-agents.html',
  styleUrl: './admin-agents.css',
})
export class AdminAgents implements OnInit {
  private svc = inject(AdminAgentService);
  agents = signal<AgentResponse[]>([]);
  filtered = signal<AgentResponse[]>([]);
  loading = signal(true);
  filterRegion = 'ALL';
  showConfirmDelete = signal(false);
  selectedAgent = signal<AgentResponse | null>(null);
  page = signal(0);
  pageSize = 10;

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.svc.getAll().subscribe(data => {
      this.agents.set(data);
      this.applyFilters();
      this.loading.set(false);
    });
  }

  applyFilters(): void {
    this.filtered.set(this.filterRegion === 'ALL' ? [...this.agents()] : this.agents().filter(a => a.region === this.filterRegion));
    this.page.set(0);
  }

  get paginated(): AgentResponse[] {
    return this.filtered().slice(this.page() * this.pageSize, (this.page() + 1) * this.pageSize);
  }

  get totalPages(): number { return Math.ceil(this.filtered().length / this.pageSize); }

  openDelete(agent: AgentResponse): void { this.selectedAgent.set(agent); this.showConfirmDelete.set(true); }

  confirmDelete(): void {
    this.svc.delete(this.selectedAgent()!.id).subscribe(() => { this.showConfirmDelete.set(false); this.load(); });
  }
}
