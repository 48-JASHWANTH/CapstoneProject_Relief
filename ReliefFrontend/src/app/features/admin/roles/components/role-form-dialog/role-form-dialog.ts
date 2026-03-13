import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoleResponse } from '../../../../../core/models/role.model';

@Component({
  selector: 'app-role-form-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './role-form-dialog.html',
  styleUrl: './role-form-dialog.css',
})
export class RoleFormDialog implements OnInit {
  @Input() role: RoleResponse | null = null;
  @Input() isEdit = false;
  @Output() save = new EventEmitter<{ name: string; description: string }>();
  @Output() cancel = new EventEmitter<void>();

  name = '';
  description = '';

  ngOnInit(): void {
    if (this.role) { this.name = this.role.name; this.description = this.role.description; }
  }

  onSave(): void {
    const trimmedName = this.name.trim();
    const trimmedDesc = this.description.trim();
    if (!trimmedName || !trimmedDesc) return;
    this.save.emit({ name: trimmedName.toUpperCase(), description: trimmedDesc });
  }
  onCancel(): void { this.cancel.emit(); }
}
