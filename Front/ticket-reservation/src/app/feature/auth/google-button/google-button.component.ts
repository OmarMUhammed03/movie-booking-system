import { AfterViewInit, Component, ElementRef, EventEmitter, Output, ViewChild, signal } from '@angular/core';
import { GoogleAuthService } from '../../../core/services/google-auth.service';

@Component({
  selector: 'app-google-button',
  standalone: true,
  templateUrl: './google-button.component.html'
})
export class GoogleButtonComponent implements AfterViewInit {
  @ViewChild('realButtonHost', { static: true }) realButtonHost!: ElementRef<HTMLDivElement>;
  @ViewChild('fakeButton', { static: true }) fakeButton!: ElementRef<HTMLDivElement>;

  /** Emits the raw Google ID token (JWT) once the user picks an account. */
  @Output() credential = new EventEmitter<string>();
  /** Emits when the Google script/button fails to initialize. */
  @Output() googleError = new EventEmitter<string>();

  readonly ready = signal(false);

  constructor(private readonly googleAuth: GoogleAuthService) {}

  async ngAfterViewInit(): Promise<void> {
    try {
      await this.googleAuth.initialize((idToken) => this.credential.emit(idToken));
      const width = this.fakeButton.nativeElement.offsetWidth || 320;
      await this.googleAuth.renderButton(this.realButtonHost.nativeElement, width);
      this.ready.set(true);
    } catch (err) {
      console.error('Google sign-in failed to initialize', err);
      this.googleError.emit('Google sign-in is unavailable right now.');
    }
  }
}