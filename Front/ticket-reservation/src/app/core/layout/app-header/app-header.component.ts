import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TokenStorageService } from '../../services/token-storage.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './app-header.component.html'
})
export class AppHeaderComponent {
  private authService = inject(AuthService);
  private tokenStorage = inject(TokenStorageService);
  private router = inject(Router);

  isLoggedIn(): boolean {
    return this.tokenStorage.isLoggedIn;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => void this.router.navigate(['/movies'])
    });
  }
}
