import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // TEMP: paste a real JWT here (from Postman login call) to test the interceptor
  // against the live backend. Replace with real token storage once login UI exists.
  private readonly tokenSignal = signal<string | null>(
    'eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhaG1lZDFAZ21haWwuY29tIiwicm9sZXMiOlsiQURNSU4iXSwidHlwZSI6IkFDQ0VTUyIsInVzZXJJZCI6ImM3ZTMxMjdkLTBjYzAtNDQ5OC1iZmJmLTg1ZWZhMjAzZGMzNyIsImlhdCI6MTc4MzM2MzYxOCwiZXhwIjoxNzgzNDUwMDE4fQ.jiydMZxCwy5VXSuNXL0wJqW5Dv3wN0FlbyNU9T-SgdW4m-iuo6yNsRh0BNrZzFjJmzzrA3Qu4aJvxjZiYnAA38g0kr28RbhIBivGatwqGE62FjjceR2DsJ3g8RSE_muiTnAUxk_sasjus3hyzMrBG3Hwzv1qIu_qGrWkQZ3YkfdPXeHtPTOeP4qjRHP0b4ntWMvfomddI5AQ0nezZMIfO-xgUGDTeXkbM15grsuof_VtWYyRVxneE3Ve0BXM4H0NaUIrlL9B3KrmcvNH3zkJZCh9ATMHmOU8DRIzLj89D1XppvcwNEK7vc6EuiMlI-VbTidaAeoRc8fxoNGLrMbB6Q'
  );

  private readonly userIdSignal = signal<string>(
    'c7e3127d-0cc0-4498-bfbf-85efa203dc37'
  );

  getToken(): string | null {
    return this.tokenSignal();
  }

  getUserId(): string {
    return this.userIdSignal();
  }
}