import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cerrar',
  templateUrl: './cerrar.component.html',
  styleUrls: ['./cerrar.component.css']
})
export class CerrarComponent implements OnInit {
  API_URL = 'http://localhost:8081/api/restaurante';
  correo: string | null = '';
  restauranteId: string | null = '';
  mensaje: string = '';
  tipoMensaje: 'success' | 'error' | '' = '';
  modalVisible: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.correo = localStorage.getItem('restauranteCorreo');
    this.restauranteId = localStorage.getItem('restauranteId');

    console.log('📦 LocalStorage:', { correo: this.correo, restauranteId: this.restauranteId });

    if (!this.correo || !this.restauranteId) {
      alert('Debes iniciar sesión primero');
      localStorage.clear();
      this.router.navigate(['/']);
    }
  }

  abrirModal() {
    this.modalVisible = true;
  }

  cerrarModal() {
    this.modalVisible = false;
  }

  confirmarCierre() {
    if (!this.restauranteId) return;

    console.log('🚨 Cerrando cuenta con ID:', this.restauranteId);

    this.http.post<any>(`${this.API_URL}/${this.restauranteId}/cerrar`, { confirm: true })
      .subscribe({
        next: (data) => {
          console.log('✅ Respuesta del servidor:', data);
          this.mostrarMensaje(data.message || 'Cuenta cerrada correctamente', 'success');
          this.modalVisible = false;

          setTimeout(() => {
            localStorage.clear();
            this.router.navigate(['/']);
          }, 2000);
        },
        error: (err) => {
          console.error('❌ Error al cerrar cuenta:', err);
          this.modalVisible = false;
          this.mostrarMensaje('Error de conexión con el servidor', 'error');
        }
      });
  }

  volver() {
    this.router.navigate(['/']);
  }

  mostrarMensaje(texto: string, tipo: 'success' | 'error') {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }
}

import { NgModule } from '@angular/core';

@NgModule({
  declarations: [CerrarComponent],
  imports: [
    CommonModule,
    FormsModule
  ],
  exports: [CerrarComponent]
})
export class RestauranteModule {}
