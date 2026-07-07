import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TokenStorageService } from './token-storage.service';

export interface ChatResponse {
  reply: string;
  conversationId: string;
}

interface ChatRequest {
  message: string;
  userId?: string;
  movieId?: string;
  conversationId?: string;
}

@Injectable({ providedIn: 'root' })
export class AiChatService {
  private http = inject(HttpClient);
  private tokenStorage = inject(TokenStorageService);

  // kept for the whole session so the bot remembers the conversation
  private conversationId: string | null = null;

  /**
   * Sends a message to ai-service. The auth interceptor attaches the JWT;
   * userId from localStorage enables personalized timing suggestions.
   * Pass movieId when the user is viewing a specific movie ("more like this").
   */
  send(message: string, movieId?: string): Observable<ChatResponse> {
    const request: ChatRequest = {
      message,
      userId: this.tokenStorage.userId ?? undefined,
      movieId,
      conversationId: this.conversationId ?? undefined
    };
    return this.http.post<ChatResponse>(`${environment.aiUrl}/chat`, request);
  }

  rememberConversation(id: string): void {
    this.conversationId = id;
  }

  resetConversation(): void {
    this.conversationId = null;
  }
}
