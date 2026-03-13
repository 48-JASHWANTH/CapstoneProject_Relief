import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUserService, CreateUserRequest } from '../../services/admin-user';
import { UserResponse } from '../../../../../core/models/user.model';
import { UserStatusDialog } from '../user-status-dialog/user-status-dialog';
import { CreateUserDialog } from '../create-user-dialog/create-user-dialog';

@Component({
  selector: 'app-admin-users',
  imports: [CommonModule, FormsModule, UserStatusDialog, CreateUserDialog],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers implements OnInit {
  private userSvc = inject(AdminUserService);
  users = signal<UserResponse[]>([]);
  filtered = signal<UserResponse[]>([]);
  loading = signal(true);

  searchText = '';
  filterRole = 'ALL';
  filterStatus = 'ALL';

  showStatusDialog = signal(false);
  showCreateDialog = signal(false);
  showConfirmDelete = signal(false);
  showViewDialog = signal(false);

  selectedUser = signal<UserResponse | null>(null);
  viewedUser = signal<UserResponse | null>(null);

  page = signal(0);
  pageSize = 10;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.userSvc.getAll().subscribe(data => {
      this.users.set(data);
      this.applyFilters();
      this.loading.set(false);
    });
  }

  applyFilters(): void {
    let result = [...this.users()];
    if (this.filterRole !== 'ALL') result = result.filter(u => u.role === this.filterRole);
    if (this.filterStatus !== 'ALL') result = result.filter(u => u.status === this.filterStatus);
    if (this.searchText.trim()) {
      const q = this.searchText.toLowerCase();
      result = result.filter(u => u.name.toLowerCase().includes(q) || u.email.toLowerCase().includes(q));
    }
    this.filtered.set(result);
    this.page.set(0);
  }

  get paginated(): UserResponse[] {
    return this.filtered().slice(this.page() * this.pageSize, (this.page() + 1) * this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filtered().length / this.pageSize);
  }

  openStatusDialog(user: UserResponse): void {
    this.selectedUser.set(user);
    this.showStatusDialog.set(true);
  }

  onStatusSaved(data: { status: string }): void {
    this.userSvc.updateStatus(this.selectedUser()!.id, data).subscribe(() => {
      this.showStatusDialog.set(false);
      this.load();
    });
  }

  onUserCreated(data: CreateUserRequest): void {
    this.userSvc.create(data).subscribe(() => {
      this.showCreateDialog.set(false);
      this.load();
    });
  }

  openViewDialog(user: UserResponse): void {
    this.viewedUser.set(user);
    this.showViewDialog.set(true);
  }

  openDeleteConfirm(user: UserResponse): void {
    this.selectedUser.set(user);
    this.showConfirmDelete.set(true);
  }

  confirmDelete(): void {
    this.userSvc.delete(this.selectedUser()!.id).subscribe(() => {
      this.showConfirmDelete.set(false);
      this.load();
    });
  }

  removeRole(user: UserResponse, role: string): void {
    this.userSvc.removeRole(user.id, role).subscribe(() => this.load());
  }

  roleChipClass(role: string): string {
    const map: Record<string, string> = {
      ADMIN: 'bg-red-100 text-red-700',
      AGENT: 'bg-[#F3F4F4] text-[#612D53]',
      CLAIMS_OFFICER: 'bg-orange-100 text-orange-700',
      CUSTOMER: 'bg-green-100 text-green-700',
    };
    return map[role] || 'bg-gray-100 text-gray-700';
  }

  statusClass(status: string): string {
    return status === 'ACTIVE' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700';
  }

  formatDate(d: string): string {
    return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
