import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent {
  API_URL = 'http://localhost:8081/api/restaurante';

  correo: string = '';
  password: string = '';
  mensaje: string = '';
  tipoMensaje: 'success' | 'error' | '' = '';

  constructor(private http: HttpClient, private router: Router) {}

  login() {
    if (!this.correo || !this.password) {
      this.mostrarMensaje('Por favor completa todos los campos', 'error');
      return;
    }

    console.log('🔄 Intentando login con:', this.correo);

    this.http.post<any>(`${this.API_URL}/login`, {
      correo: this.correo,
      password: this.password
    }).subscribe({
      next: (data) => {
        console.log('✅ Respuesta del servidor:', data);

        if (data.userId) {
          // Guardar en localStorage
          localStorage.setItem('restauranteCorreo', this.correo);
          localStorage.setItem('restauranteId', data.userId);

          this.mostrarMensaje('Login exitoso. Redirigiendo...', 'success');

          setTimeout(() => {
            this.router.navigate(['/cerrar']);
          }, 1000);
        } else {
          this.mostrarMensaje(data.message || 'Credenciales incorrectas', 'error');
        }
      },
      error: (err) => {
        console.error('❌ Error en login:', err);
        this.mostrarMensaje('Error de conexión con el servidor', 'error');
      }
    });
  }

  mostrarMensaje(texto: string, tipo: 'success' | 'error') {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }
}

import { NgModule } from '@angular/core';

@NgModule({
  declarations: [IndexComponent],
  imports: [
    CommonModule,
    FormsModule
  ],
  exports: [IndexComponent]
})
export class RestauranteModule {}
