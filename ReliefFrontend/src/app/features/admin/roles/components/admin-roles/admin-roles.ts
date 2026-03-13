import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminRoleService } from '../../services/admin-role';
import { RoleResponse } from '../../../../../core/models/role.model';
import { RoleFormDialog } from '../role-form-dialog/role-form-dialog';

@Component({
  selector: 'app-admin-roles',
  imports: [CommonModule, FormsModule, RoleFormDialog],
  templateUrl: './admin-roles.html',
  styleUrl: './admin-roles.css',
})
export class AdminRoles implements OnInit {
  private svc = inject(AdminRoleService);
  roles = signal<RoleResponse[]>([]);
  loading = signal(true);
  showFormDialog = signal(false);
  showConfirmDelete = signal(false);
  selectedRole = signal<RoleResponse | null>(null);
  isEdit = signal(false);

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading.set(true);
    this.svc.getAll().subscribe(data => { this.roles.set(data); this.loading.set(false); });
  }

  openCreate(): void { this.selectedRole.set(null); this.isEdit.set(false); this.showFormDialog.set(true); }
  openEdit(role: RoleResponse): void { this.selectedRole.set(role); this.isEdit.set(true); this.showFormDialog.set(true); }
  openDelete(role: RoleResponse): void { this.selectedRole.set(role); this.showConfirmDelete.set(true); }

  onFormSaved(data: { name: string; description: string }): void {
    const obs = this.isEdit() ? this.svc.update(this.selectedRole()!.id, data) : this.svc.create(data);
    obs.subscribe(() => { this.showFormDialog.set(false); this.load(); });
  }

  confirmDelete(): void {
    this.svc.delete(this.selectedRole()!.id).subscribe(() => { this.showConfirmDelete.set(false); this.load(); });
  }

  roleChipClass(name: string): string {
    const map: Record<string, string> = { ADMIN: 'bg-red-100 text-red-700', AGENT: 'bg-[#F3F4F4] text-[#612D53]', CLAIMS_OFFICER: 'bg-orange-100 text-orange-700', CUSTOMER: 'bg-green-100 text-green-700' };
    return map[name] || 'bg-gray-100 text-gray-600';
  }
}
