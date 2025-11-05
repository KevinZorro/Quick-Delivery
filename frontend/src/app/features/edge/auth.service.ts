import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

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
  private baseUrl = 'http://localhost:8083/api/auth';
  private tokenKey = 'quick-delivery-token';
  private userIdKey = 'quick-delivery-userId';
  private userNameKey = 'quick-delivery-userName';
  private userRoleKey = 'quick-delivery-userRole';

  constructor(private http: HttpClient) {}

  login(correo: string, contraseña: string): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/login`, { correo, contraseña })
      .pipe(
        tap(res => {
          console.log('═══════════════════════════════════════');
          console.log('✅ LOGIN EXITOSO');
          console.log('═══════════════════════════════════════');
          console.log('📦 Token:', res.token);
          console.log('🆔 User ID:', res.userId);
          console.log('👤 Nombre:', res.nombre);
          console.log('📧 Correo:', res.correo);
          console.log('🎭 Rol:', res.rol);
          console.log('═══════════════════════════════════════');
          console.log('🔗 VERIFICA TU TOKEN EN: https://jwt.io');
          console.log('📋 Copia este token completo:');
          console.log(res.token);
          console.log('═══════════════════════════════════════');

          // Guardar todo en localStorage
          if (res.token) {
            localStorage.setItem(this.tokenKey, res.token);
          }
          if (res.userId) {
            localStorage.setItem(this.userIdKey, res.userId);
          }
          if (res.nombre) {
            localStorage.setItem(this.userNameKey, res.nombre);
          }
          if (res.rol) {
            localStorage.setItem(this.userRoleKey, res.rol);
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

  getUserId(): string | null {
    return localStorage.getItem(this.userIdKey);
  }

  getUserName(): string | null {
    return localStorage.getItem(this.userNameKey);
  }

  getUserRole(): string | null {
    return localStorage.getItem(this.userRoleKey);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userIdKey);
    localStorage.removeItem(this.userNameKey);
    localStorage.removeItem(this.userRoleKey);
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

  getCurrentUser(): {
    userId: string | null;
    nombre: string | null;
    rol: string | null;
  } {
    return {
      userId: this.getUserId(),
      nombre: this.getUserName(),
      rol: this.getUserRole()
    };
  }

  // ⭐ MÉTODO CORREGIDO - Usar baseUrl en lugar de apiUrl
  loginWithGoogle(idToken: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/google`, { token: idToken })
      .pipe(
        tap(res => {
          if (res.status === 'OK') {
            console.log('✅ Login con Google exitoso:', res);
            // Aquí puedes guardar la información del usuario si es necesario
            // localStorage.setItem('google-user', JSON.stringify(res));
          }
        })
      );
  }
}