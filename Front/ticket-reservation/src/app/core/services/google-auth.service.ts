import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

declare global {
  interface Window {
    google: any;
  }
}

const SCRIPT_ID = 'google-identity-services';

/**
 * Wrapper around Google Identity Services.
 *
 * Uses renderButton() (a real "Sign in with Google" button) rather than the
 * One Tap prompt() flow. prompt() depends on FedCM + an active Google session
 * in the browser and fails hard with "origin not allowed" / "accounts list is
 * empty" in far more situations (incognito, blocked 3P cookies, no signed-in
 * Google account, etc). renderButton() is the flow you want behind an explicit
 * "Continue with Google" button.
 */
@Injectable({ providedIn: 'root' })
export class GoogleAuthService {
  private scriptLoadPromise: Promise<void> | null = null;
  private initialized = false;

  async initialize(onCredential: (idToken: string) => void): Promise<void> {
      console.log('GSI client_id in use:', environment.googleClientId); // TEMP DEBUG

    await this.loadScript();

    if (!window.google?.accounts?.id) {
      throw new Error('Google Identity Services failed to load.');
    }

    window.google.accounts.id.initialize({
      client_id: environment.googleClientId,
      callback: (response: any) => onCredential(response.credential),
      auto_select: false,
      ux_mode: 'popup',
      cancel_on_tap_outside: true
    });

    this.initialized = true;
  }

  /** Renders Google's real button into `container`. Call after `initialize()`. */
  async renderButton(container: HTMLElement, widthPx = 320): Promise<void> {
    await this.loadScript();

    if (!this.initialized || !window.google?.accounts?.id) {
      return;
    }

    window.google.accounts.id.renderButton(container, {
      type: 'standard',
      theme: 'filled_black',
      size: 'large',
      shape: 'pill',
      width: widthPx
    });
  }

  private loadScript(): Promise<void> {
    if (this.scriptLoadPromise) {
      return this.scriptLoadPromise;
    }

    this.scriptLoadPromise = new Promise<void>((resolve, reject) => {
      // Already available (e.g. loaded via the static <script> tag in index.html).
      if (window.google?.accounts?.id) {
        resolve();
        return;
      }

      // A script tag for GSI already exists (static tag in index.html) — wait on it
      // instead of injecting a duplicate.
      const existing = document.querySelector<HTMLScriptElement>(
        'script[src="https://accounts.google.com/gsi/client"]'
      );
      if (existing) {
        existing.addEventListener('load', () => resolve());
        existing.addEventListener('error', () =>
          reject(new Error('Failed to load Google Identity Services script.'))
        );
        if (window.google?.accounts?.id) {
          resolve();
        }
        return;
      }

      const script = document.createElement('script');
      script.id = SCRIPT_ID;
      script.src = 'https://accounts.google.com/gsi/client';
      script.async = true;
      script.defer = true;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Failed to load Google Identity Services script.'));
      document.head.appendChild(script);
    });

    return this.scriptLoadPromise;
  }
}