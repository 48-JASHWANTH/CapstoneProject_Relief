import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminClaimsOfficerService, OfficerClaimSummary } from '../../services/admin-claims-officer';
import { AdminUserService } from '../../../users/services/admin-user';
import { UserResponse } from '../../../../../core/models/user.model';

@Component({
  selector: 'app-admin-claims-officer-detail',
  imports: [CommonModule],
  templateUrl: './admin-claims-officer-detail.html',
  styleUrl: './admin-claims-officer-detail.css',
})
export class AdminClaimsOfficerDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private svc = inject(AdminClaimsOfficerService);
  private userSvc = inject(AdminUserService);
  officer = signal<UserResponse | null>(null);
  claims = signal<OfficerClaimSummary[]>([]);
  loading = signal(true);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.userSvc.getAll().subscribe(users => {
      this.officer.set(users.find(u => u.id === id) ?? null);
      this.svc.getClaimsByOfficer(id).subscribe(claims => {
        this.claims.set(claims);
        this.loading.set(false);
      });
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/claims-officers']);
  }

  statusClass(status: string): string {
    const map: Record<string, string> = {
      APPROVED: 'bg-green-100 text-green-700',
      UNDER_REVIEW: 'bg-yellow-100 text-yellow-700',
      PENDING: 'bg-[#F3F4F4] text-[#612D53]',
      REJECTED: 'bg-red-100 text-red-700',
      SETTLED: 'bg-[#F3F4F4] text-[#612D53]',
    };
    return map[status] || 'bg-gray-100 text-gray-600';
  }

  disasterClass(type: string): string {
    const map: Record<string, string> = {
      FLOOD: 'bg-[#F3F4F4] text-[#612D53]',
      EARTHQUAKE: 'bg-[#F3F4F4] text-[#612D53]',
      CYCLONE: 'bg-[#F3F4F4] text-[#612D53]',
      HURRICANE: 'bg-red-100 text-red-700',
      WILDFIRE: 'bg-[#F3F4F4] text-[#612D53]',
      LANDSLIDE: 'bg-stone-100 text-stone-700',
    };
    return map[type] || 'bg-gray-100 text-gray-600';
  }

  formatDate(d: string | null): string {
    if (!d) return '—';
    return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  formatCurrency(v: number): string {
    return '₹' + v.toLocaleString();
  }

  get userStatusClass(): string {
    return this.officer()?.status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500';
  }

  get resolvedCount(): number {
    return this.claims().filter(c => c.status === 'APPROVED' || c.status === 'REJECTED' || c.status === 'SETTLED').length;
  }
}
