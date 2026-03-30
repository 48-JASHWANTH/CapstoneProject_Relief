import {
  Component, inject, signal, ElementRef, ViewChild,
  AfterViewChecked, OnInit
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService, ChatRequest, ChatResponse } from '../../../core/services/chatbot.service';
import { AuthService } from '../../../core/services/auth';

interface ChatMessage {
  sender: 'user' | 'bot';
  text: string;
  time: string;
}

const ROLE_CHIPS: Record<string, string[]> = {
  CUSTOMER:       ['My policies', 'My claims status', 'Next payment due', 'How to file a claim'],
  AGENT:          ['My policy portfolio', 'Pending approvals', 'How to adjust premium', 'AI prediction tool'],
  CLAIMS_OFFICER: ['My assigned claims', 'Pending cases', 'How to approve a claim', 'AI damage analysis'],
  ADMIN:          ['System overview', 'Pending policies', 'Unassigned claims', 'User management'],
  GUEST:          ['What is Relief?', 'Disaster coverage types', 'How to register', 'How claims work'],
};

const ROLE_LABELS: Record<string, string> = {
  CUSTOMER:       'Customer',
  AGENT:          'Agent',
  CLAIMS_OFFICER: 'Claims Officer',
  ADMIN:          'Admin',
};

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.html',
  styleUrls: ['./chatbot.component.css'],
})
export class ChatbotComponent implements OnInit, AfterViewChecked {

  private chatbotService = inject(ChatbotService);
  private authService    = inject(AuthService);

  isOpen        = signal<boolean>(false);
  messages      = signal<ChatMessage[]>([]);
  newMessage    = signal<string>('');
  isLoading     = signal<boolean>(false);
  conversationId = signal<string | undefined>(undefined);
  showUnread    = signal<boolean>(true);

  // Derived auth info
  isLoggedIn = false;
  userRole   = 'GUEST';
  userName   = '';
  roleLabel  = '';
  chips: string[] = [];

  @ViewChild('chatContainer') private chatContainer!: ElementRef;

  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      const raw = this.authService.getRole() ?? 'GUEST';
      // backend stores as "CUSTOMER", JWT returns role without "ROLE_" prefix per JwtUtil
      this.userRole = raw.replace('ROLE_', '');
      this.userName = this.authService.getUserEmail().split('@')[0];
    }
    this.roleLabel = ROLE_LABELS[this.userRole] ?? '';
    this.chips     = ROLE_CHIPS[this.userRole] ?? ROLE_CHIPS['GUEST'];
  }

  get roleBadgeClass(): string {
    return this.userRole.toLowerCase().replace('_', '_');
  }

  toggleChat() {
    this.isOpen.update(v => !v);
    this.showUnread.set(false);

    if (this.isOpen() && this.messages().length === 0) {
      const greeting = this.buildGreeting();
      this.messages.set([{ sender: 'bot', text: greeting, time: this.now() }]);
    }
  }

  private buildGreeting(): string {
    if (!this.isLoggedIn) {
      return `👋 Welcome to Relief AI Assistant!\n\nI can help you learn about disaster insurance, coverage options, and how to get protected. How can I assist you today?`;
    }
    switch (this.userRole) {
      case 'CUSTOMER':
        return `👋 Hello${this.userName ? ', ' + this.userName : ''}!\n\nI'm your personal Relief AI Assistant. I can answer questions about your policies, claim statuses, payment schedules, and more.\n\nWhat would you like to know?`;
      case 'AGENT':
        return `👋 Welcome back${this.userName ? ', ' + this.userName : ''}!\n\nI can give you a quick overview of your policy portfolio, pending approvals, and assist with underwriting queries.\n\nWhat do you need?`;
      case 'CLAIMS_OFFICER':
        return `👋 Hello${this.userName ? ', ' + this.userName : ''}!\n\nI have a summary of your assigned claims ready. I can help you review cases, understand damage reports, and guide you through approvals.\n\nHow can I help?`;
      case 'ADMIN':
        return `👋 Welcome, Administrator!\n\nI have system-wide stats available. Ask me about policy counts, claim statuses, unassigned cases, or anything else you need.\n\nWhat would you like to review?`;
      default:
        return `👋 Hi there! I'm the Relief AI Assistant. How can I help you today?`;
    }
  }

  sendMessage(text?: string) {
    const msgText = (text ?? this.newMessage()).trim();
    if (!msgText || this.isLoading()) return;

    this.messages.update(msgs => [...msgs, { sender: 'user', text: msgText, time: this.now() }]);
    this.newMessage.set('');
    this.isLoading.set(true);

    if (!this.conversationId()) {
      this.conversationId.set(crypto.randomUUID());
    }

    const request: ChatRequest = {
      message: msgText,
      conversationId: this.conversationId(),
    };

    this.chatbotService.sendMessage(request).subscribe({
      next: (res: ChatResponse) => {
        this.messages.update(msgs => [...msgs, { sender: 'bot', text: res.response, time: this.now() }]);
        this.isLoading.set(false);
      },
      error: () => {
        this.messages.update(msgs => [...msgs, {
          sender: 'bot',
          text: '⚠️ Sorry, I\'m having trouble connecting. Please check your network and try again.',
          time: this.now()
        }]);
        this.isLoading.set(false);
      }
    });
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendChip(chip: string) {
    this.sendMessage(chip);
  }

  private now(): string {
    return new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      if (this.chatContainer) {
        this.chatContainer.nativeElement.scrollTop =
          this.chatContainer.nativeElement.scrollHeight;
      }
    } catch { }
  }
}
