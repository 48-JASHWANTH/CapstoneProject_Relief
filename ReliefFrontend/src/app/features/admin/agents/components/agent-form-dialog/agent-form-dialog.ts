import { Component, EventEmitter, Input, OnInit, Output, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgentResponse } from '../../../../../core/models/agent.model';
import { AdminUserService } from '../../../users/services/admin-user';
import { UserResponse } from '../../../../../core/models/user.model';

@Component({
  selector: 'app-agent-form-dialog',
  imports: [CommonModule, FormsModule],
  templateUrl: './agent-form-dialog.html',
  styleUrl: './agent-form-dialog.css',
})
export class AgentFormDialog implements OnInit {
  @Input() agent: AgentResponse | null = null;
  @Input() isEdit = false;
  @Output() save = new EventEmitter<{ userId: number; licenseNumber: string; region: string }>();
  @Output() cancel = new EventEmitter<void>();

  userId: number = 0;
  licenseNumber = '';
  region = 'NORTH';
  users = signal<UserResponse[]>([]);
  regions = ['NORTH', 'SOUTH', 'EAST', 'WEST', 'CENTRAL'];

  private userSvc = inject(AdminUserService);

  ngOnInit(): void {
    this.userSvc.getAll().subscribe(u => this.users.set(u));
    if (this.agent) {
      this.userId = this.agent.userId;
      this.licenseNumber = this.agent.licenseNumber;
      this.region = this.agent.region;
    }
  }

  get isValid(): boolean {
    return this.userId > 0 && this.licenseNumber.trim().length >= 3 && !!this.region;
  }

  onSave(): void {
    if (!this.isValid) return;
    this.save.emit({ userId: this.userId, licenseNumber: this.licenseNumber.trim(), region: this.region });
  }

  onCancel(): void { this.cancel.emit(); }
}
