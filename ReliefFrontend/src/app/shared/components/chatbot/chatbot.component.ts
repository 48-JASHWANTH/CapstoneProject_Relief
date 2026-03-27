import { Component, inject, signal, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService, ChatRequest, ChatResponse } from '../../../core/services/chatbot.service';

interface ChatMessage {
  sender: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.html',
})
export class ChatbotComponent implements AfterViewChecked {
  private chatbotService = inject(ChatbotService);
  
  isOpen = signal<boolean>(false);
  messages = signal<ChatMessage[]>([]);
  newMessage = signal<string>('');
  isLoading = signal<boolean>(false);
  conversationId = signal<string | undefined>(undefined);

  @ViewChild('chatContainer') private chatContainer!: ElementRef;

  toggleChat() {
    this.isOpen.update((v: boolean) => !v);
    if (this.isOpen() && this.messages().length === 0) {
      this.messages.set([{ sender: 'bot', text: 'Hi! I am the Relief AI assistant. How can I help you with your disaster insurance today?' }]);
    }
  }

  sendMessage() {
    if (!this.newMessage().trim() || this.isLoading()) return;

    const userText = this.newMessage().trim();
    this.messages.update((msgs: ChatMessage[]) => [...msgs, { sender: 'user', text: userText }]);
    this.newMessage.set('');
    this.isLoading.set(true);

    if (!this.conversationId()) {
      this.conversationId.set(crypto.randomUUID());
    }

    const request: ChatRequest = { 
      message: userText, 
      conversationId: this.conversationId() 
    };

    this.chatbotService.sendMessage(request).subscribe({
      next: (res: ChatResponse) => {
        this.messages.update((msgs: ChatMessage[]) => [...msgs, { sender: 'bot', text: res.response }]);
        this.isLoading.set(false);
      },
      error: () => {
        this.messages.update((msgs: ChatMessage[]) => [...msgs, { sender: 'bot', text: 'Sorry, I am having trouble connecting to the server. Please check your network or try again later.' }]);
        this.isLoading.set(false);
      }
    });
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    try {
      if (this.chatContainer) {
        this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
      }
    } catch(err) { }
  }
}
