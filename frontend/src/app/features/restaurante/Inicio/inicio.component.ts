import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RestauranteService, Restaurante } from '../shared/services/restaurante.service';
import { AuthService } from '../../edge/auth.service';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.css']
})
export class InicioComponent implements OnInit {
  restaurante: Restaurante | null = null;
  loading = true;
  errorMessage: string | null = null;

  // Simulamos el ID del usuario logueado (deberías obtenerlo del token o servicio de auth)
  usuarioId = '123e4567-e89b-12d3-a456-426614174000'; // Cambia esto por el ID real del usuario

  constructor(
    private restauranteService: RestauranteService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarRestaurante();
  }

  cargarRestaurante(): void {
    this.loading = true;
    this.restauranteService.listarPorUsuarioId(this.usuarioId).subscribe({
      next: (restaurantes) => {
        if (restaurantes && restaurantes.length > 0) {
          this.restaurante = restaurantes[0]; // Tomamos el primer restaurante
        }
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar el restaurante';
        this.loading = false;
        console.error(err);
      }
    });
  }

  irAMenuProductos(): void {
    if (this.restaurante) {
      this.router.navigate(['/menuProducto'], { 
        queryParams: { restauranteId: this.restaurante.id } 
      });
    }
  }

  cerrarCuenta(): void {
    if (confirm('¿Estás seguro de que deseas cerrar tu cuenta? Esta acción no se puede deshacer.')) {
      this.router.navigate(['/cerrar']);
    }
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
