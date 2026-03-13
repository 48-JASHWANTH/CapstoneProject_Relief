import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminClaimsOfficerService } from '../../services/admin-claims-officer';
import { UserResponse } from '../../../../../core/models/user.model';

@Component({
  selector: 'app-admin-claims-officers',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-claims-officers.html',
  styleUrl: './admin-claims-officers.css',
})
export class AdminClaimsOfficers implements OnInit {
  private svc = inject(AdminClaimsOfficerService);
  private router = inject(Router);
  officers = signal<UserResponse[]>([]);
  filtered = signal<UserResponse[]>([]);
  loading = signal(true);
  searchText = '';
  filterStatus = 'ALL';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.svc.getOfficers().subscribe(data => {
      this.officers.set(data);
      this.applyFilters();
      this.loading.set(false);
    });
  }

  applyFilters(): void {
    let result = [...this.officers()];
    if (this.filterStatus !== 'ALL') result = result.filter(u => u.status === this.filterStatus);
    if (this.searchText.trim()) {
      const q = this.searchText.toLowerCase();
      result = result.filter(u => u.name.toLowerCase().includes(q) || u.email.toLowerCase().includes(q));
    }
    this.filtered.set(result);
  }

  viewDetail(id: number): void {
    this.router.navigate(['/admin/claims-officers', id]);
  }

  statusClass(status: string): string {
    return status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500';
  }

  formatDate(d: string | null): string {
    if (!d) return '—';
    return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
