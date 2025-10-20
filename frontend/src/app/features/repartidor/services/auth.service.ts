import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest } from '../models/login-request';
import { JwtResponse } from '../models/jwt-response';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Ajusta el puerto si tu backend no corre en 8080
  private apiUrl = 'http://localhost:8080/api/auth/repartidor/login'; 

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: LoginRequest): Observable<JwtResponse> {
    // Hace la llamada POST al endpoint de tu AuthController.java
    return this.http.post<JwtResponse>(this.apiUrl, credentials); 
  }

  saveTokenAndUser(token: string, email: string): void {
    localStorage.setItem('jwt_token', token);
    localStorage.setItem('user_email', email);
  }

  /**
   * Cierra la sesión del usuario.
   * 1. Elimina el token y el email del localStorage.
   * 2. Redirige al usuario a la página de login.
   */
  logout(): void {
    // 1. ELIMINAR LOS DATOS DE AUTENTICACIÓN
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_email');
    
    // 2. REDIRIGIR AL LOGIN
    this.router.navigate(['/repartidor']); // Redirige a la ruta principal del módulo
  }

  /**
   * Comprueba si el usuario tiene un token almacenado.
   */
  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt_token');
  }
}