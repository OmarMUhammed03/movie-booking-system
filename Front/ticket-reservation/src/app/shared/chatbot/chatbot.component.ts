import { Component, ElementRef, effect, inject, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs';
import { AiChatService } from '../../core/services/ai-chat.service';
import { TokenStorageService } from '../../core/services/token-storage.service';

interface ChatMessage {
  from: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chatbot',
  imports: [FormsModule],
  templateUrl: './chatbot.component.html'
})
export class ChatbotComponent {
  private aiChat = inject(AiChatService);
  private tokenStorage = inject(TokenStorageService);
  private router = inject(Router);

  private messagesBox = viewChild<ElementRef<HTMLDivElement>>('messagesBox');

  private currentUrl = toSignal(
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map((e) => e.urlAfterRedirects),
      startWith(this.router.url)
    ),
    { initialValue: this.router.url }
  );

  open = signal(false);
  draft = signal('');
  thinking = signal(false);
  messages = signal<ChatMessage[]>([
    { from: 'bot', text: 'Hi! I\'m CineBot 🎬 Ask me for a movie recommendation — mood, genre, timing, anything.' }
  ]);

  constructor() {
    // keep the newest message in view
    effect(() => {
      this.messages();
      this.thinking();
      const box = this.messagesBox()?.nativeElement;
      if (box) {
        setTimeout(() => box.scrollTo({ top: box.scrollHeight, behavior: 'smooth' }));
      }
    });
  }

  /** hidden on the auth pages (and the '' redirect that lands on login) */
  get visible(): boolean {
    const url = this.currentUrl().split('?')[0];
    return url !== '/login' && url !== '/register' && url !== '/';
  }

  get isLoggedIn(): boolean {
    return this.tokenStorage.isLoggedIn;
  }

  toggle(): void {
    this.open.update((v) => !v);
  }

  send(): void {
    const text = this.draft().trim();
    if (!text || this.thinking()) {
      return;
    }
    this.draft.set('');
    this.messages.update((m) => [...m, { from: 'user', text }]);
    this.thinking.set(true);

    this.aiChat.send(text).subscribe({
      next: (res) => {
        this.aiChat.rememberConversation(res.conversationId);
        this.messages.update((m) => [...m, { from: 'bot', text: res.reply }]);
        this.thinking.set(false);
      },
      error: () => {
        this.messages.update((m) => [
          ...m,
          { from: 'bot', text: 'Sorry, I couldn\'t reach the recommendation service. Please try again in a moment.' }
        ]);
        this.thinking.set(false);
      }
    });
  }
}
