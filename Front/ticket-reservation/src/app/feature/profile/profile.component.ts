import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { Location } from '@angular/common'; // 1. Import Location

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private location = inject(Location); // 2. Inject Location

  resource = this.userService.profileResource;

  ngOnInit(): void {
    this.userService.authUserId.set(this.authService.getUserId());
  }

  getEmail(): string {
    return this.authService.getUserEmail();
  }

  getRole(): string {
    return this.authService.getUserRole();
  }

  // 3. Add method to navigate back
  goBack(): void {
    this.location.back();
  }
}