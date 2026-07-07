import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { GoogleButtonComponent } from '../google-button/google-button.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, GoogleButtonComponent],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly error = signal('');

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    rememberMe: [true]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Please fill in a valid email and password.');
      return;
    }

    this.error.set('');
    const { email, password } = this.form.getRawValue();

    this.authService.login({ email, password }).subscribe({
      next: () => this.router.navigateByUrl('/reservations'),
      error: (err) => {
        console.error('Login failed', err);
        this.error.set('Invalid email or password');
      }
    });
  }

  onGoogleCredential(idToken: string): void {
    this.error.set('');
    this.authService.loginWithGoogle(idToken).subscribe({
      next: () => this.router.navigateByUrl('/reservations'),
      error: (err) => {
        console.error('Google login failed', err);
        this.error.set('Google sign-in failed');
      }
    });
  }

  onGoogleError(message: string): void {
    this.error.set(message);
  }
}