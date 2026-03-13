import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserResponse } from '../../../../../core/models/user.model';

@Component({
  selector: 'app-user-status-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-status-dialog.html',
  styleUrl: './user-status-dialog.css',
})
export class UserStatusDialog implements OnInit {
  @Input() user!: UserResponse;
  @Output() save = new EventEmitter<{ status: string }>();
  @Output() cancel = new EventEmitter<void>();

  selectedStatus = 'ACTIVE';

  ngOnInit(): void {
    this.selectedStatus = this.user?.status || 'ACTIVE';
  }

  onSave(): void {
    this.save.emit({ status: this.selectedStatus });
  }

  onCancel(): void {
    this.cancel.emit();
  }
}
