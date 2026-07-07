import { HttpClient } from '@angular/common/http';
import { inject, Injectable, resource, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserProfile } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  authUserId = signal<string | null>(null);

  profileResource = resource({
    params: () => this.authUserId(),
    loader: async ({ params }) => {
      if (!params) {
        return null;
      }
      return firstValueFrom(
        this.http.get<UserProfile>(`${environment.userUrl}/users/auth/${params}`)
      );
    }
  });
  
}
