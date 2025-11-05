import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface UserResponse {
  token: string;
  userId: string;      // ⭐ AGREGAR
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
  private userIdKey = 'quick-delivery-userId';        // ⭐ AGREGAR
  private userNameKey = 'quick-delivery-userName';    // ⭐ AGREGAR
  private userRoleKey = 'quick-delivery-userRole';    // ⭐ AGREGAR

  constructor(private http: HttpClient) {}

  login(correo: string, contraseña: string): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.baseUrl}/login`, { correo, contraseña })
      .pipe(
        tap(res => {
          console.log('═══════════════════════════════════════');
          console.log('✅ LOGIN EXITOSO');
          console.log('═══════════════════════════════════════');
          console.log('📦 Token:', res.token);
          console.log('🆔 User ID:', res.userId);              // ⭐ LOG
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
            localStorage.setItem(this.userIdKey, res.userId);    // ⭐ GUARDAR
          }
          if (res.nombre) {
            localStorage.setItem(this.userNameKey, res.nombre);  // ⭐ GUARDAR
          }
          if (res.rol) {
            localStorage.setItem(this.userRoleKey, res.rol);     // ⭐ GUARDAR
          }
        })
      );
  }

  verificarCorreo(correo: string): Observable<boolean> {
    return this.http.post<boolean>(`${this.baseUrl}/verificar-correo`, { correo });
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, data);
  }

  // ⭐ MÉTODOS ACTUALIZADOS Y NUEVOS

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
    localStorage.removeItem(this.userIdKey);     // ⭐ LIMPIAR
    localStorage.removeItem(this.userNameKey);   // ⭐ LIMPIAR
    localStorage.removeItem(this.userRoleKey);   // ⭐ LIMPIAR
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // ⭐ NUEVOS MÉTODOS DE UTILIDAD

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
}
