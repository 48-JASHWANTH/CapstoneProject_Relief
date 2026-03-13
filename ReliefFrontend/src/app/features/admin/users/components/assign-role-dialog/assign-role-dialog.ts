import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserResponse } from '../../../../../core/models/user.model';

@Component({
  selector: 'app-assign-role-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-role-dialog.html',
  styleUrl: './assign-role-dialog.css',
})
export class AssignRoleDialog implements OnInit {
  @Input() user!: UserResponse;
  @Input() availableRoles: string[] = [];
  @Output() save = new EventEmitter<{ roleName: string }>();
  @Output() cancel = new EventEmitter<void>();

  selectedRole: string = '';

  ngOnInit(): void {
    this.selectedRole = this.user?.role || '';
  }

  selectRole(role: string): void {
    this.selectedRole = role;
  }

  onSave(): void {
    this.save.emit({ roleName: this.selectedRole });
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
