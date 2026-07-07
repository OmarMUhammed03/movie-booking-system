import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { AppHeaderComponent } from './core/layout/app-header/app-header.component';
import { ChatbotComponent } from './shared/chatbot/chatbot.component';

const HIDDEN_NAVBAR_ROUTES = ['/login', '/register'];

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AppHeaderComponent, ChatbotComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private router = inject(Router);

  showNavbar = signal(!HIDDEN_NAVBAR_ROUTES.includes(this.router.url));

  constructor() {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.showNavbar.set(!HIDDEN_NAVBAR_ROUTES.includes(event.urlAfterRedirects));
      });
  }
}
