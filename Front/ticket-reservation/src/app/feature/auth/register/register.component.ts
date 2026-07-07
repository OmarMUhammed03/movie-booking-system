import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { GoogleButtonComponent } from '../google-button/google-button.component';
import { SignUpRequest } from '../../../core/models/auth.model';

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  return password === confirmPassword ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, GoogleButtonComponent],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly error = signal('');

  readonly form = this.fb.group(
    {
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      phone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    },
    { validators: passwordMatchValidator }
  );

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set(
        this.form.hasError('passwordMismatch')
          ? 'Passwords do not match.'
          : 'Please fill in all fields correctly.'
      );
      return;
    }

    this.error.set('');
    const request: SignUpRequest = this.form.getRawValue() as SignUpRequest;

    this.authService.register(request).subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: (err) => {
        console.error('Registration failed', err);
        this.error.set('Registration failed');
      }
    });
  }

  onGoogleCredential(idToken: string): void {
    this.error.set('');
    this.authService.loginWithGoogle(idToken).subscribe({
      next: () => this.router.navigateByUrl('/reservations'),
      error: (err) => {
        console.error('Google sign-up failed', err);
        this.error.set('Google sign-in failed');
      }
    });
  }

  onGoogleError(message: string): void {
    this.error.set(message);
  }
}