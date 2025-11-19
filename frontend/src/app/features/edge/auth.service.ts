import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';  // Ajusta ruta si es necesario
export interface UserResponse {
  token: string;
  userId: string;
  nombre: string;
  correo: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = `${environment.edgeApi}/api/auth`;
  private tokenKey = 'quick-delivery-token';
  private userIdKey = 'quick-delivery-userId';
  private userNameKey = 'quick-delivery-userName';
  private userRoleKey = 'quick-delivery-userRole';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {}

  login(correo: string, contraseña: string): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/login`, { correo, contraseña })
      .pipe(
        tap(res => {
          if (isPlatformBrowser(this.platformId)) {
            if (res.token) localStorage.setItem(this.tokenKey, res.token);
            if (res.userId) localStorage.setItem(this.userIdKey, res.userId);
            if (res.nombre) localStorage.setItem(this.userNameKey, res.nombre);
            if (res.rol) localStorage.setItem(this.userRoleKey, res.rol);
          }
        })
      );
  }

  register(data: any): Observable<any> {
      console.log('Registro usando URL:', this.baseUrl + '/register');
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  getUserId(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(this.userIdKey);
    }
    return null;
  }

  getUserName(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(this.userNameKey);
    }
    return null;
  }

  getUserRole(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(this.userRoleKey);
    }
    return null;
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userIdKey);
      localStorage.removeItem(this.userNameKey);
      localStorage.removeItem(this.userRoleKey);
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isCliente(): boolean {
    return this.getUserRole() === 'CLIENTE';
  }

  isRestaurante(): boolean {
    return this.getUserRole() === 'RESTAURANTE';
  }

  isRepartidor(): boolean {
    return this.getUserRole() === 'REPARTIDOR';
  }

  getCurrentUser(): { userId: string | null; nombre: string | null; rol: string | null } {
    return {
      userId: this.getUserId(),
      nombre: this.getUserName(),
      rol: this.getUserRole()
    };
  }

recuperarContrasena(correo: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/recuperar-contrasena`, { correo });
}

validarToken(token: string): Observable<any> {
  return this.http.get(`${this.baseUrl}/reset-password`, { params: { token } });
}

cambiarContrasena(token: string, nuevaContrasena: string): Observable<any> {
  return this.http.post(`${this.baseUrl}/reset-password`, { token, nuevaContrasena });
}

}
