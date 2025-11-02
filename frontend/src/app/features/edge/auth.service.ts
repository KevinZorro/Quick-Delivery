import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface UserResponse {
  token: string;
  nombre: string;
  correo: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8083/api/auth';
  private tokenKey = 'quick-delivery-token';

  constructor(private http: HttpClient) {}

  login(correo: string, contraseña: string): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/login`, { correo, contraseña })
      .pipe(
        tap(res => {
          if(res.token) {
            localStorage.setItem(this.tokenKey, res.token);
          }
        })
      );
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
