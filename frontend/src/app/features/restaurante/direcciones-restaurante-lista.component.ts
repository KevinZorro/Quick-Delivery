import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { DireccionRestauranteService, DireccionRestaurante } from './direcciones-restaurante.service';
import { HeaderComponent } from '../cliente/header.component';

@Component({
  selector: 'app-direcciones-restaurante-lista',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './direcciones-restaurante-lista.component.html'
})
export class DireccionesRestauranteListaComponent implements OnInit {
  direcciones: DireccionRestaurante[] = [];
  loading = true;
  errorMessage: string | null = null;
  usuarioId: string = '';

  constructor(
    private direccionService: DireccionRestauranteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // ⭐ Obtener el userId del localStorage (restaurante)
    const restauranteId = localStorage.getItem('quick-delivery-userId');
    
    if (!restauranteId) {
      this.errorMessage = 'Error: No se pudo identificar el restaurante. Por favor inicia sesión nuevamente.';
      this.loading = false;
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      return;
    }

    this.usuarioId = restauranteId;
    this.loadDirecciones();
  }

  loadDirecciones(): void {
    this.direccionService.getDireccionesByUsuario(this.usuarioId).subscribe({
      next: (data) => {
        this.direcciones = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar direcciones';
        this.loading = false;
        console.error(err);
      }
    });
  }

  editarDireccion(id: string): void {
    this.router.navigate(['/restaurante/direcciones/editar', id]);
  }

  eliminarDireccion(id: string): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta dirección?')) {
      this.direccionService.eliminarDireccion(id).subscribe({
        next: () => {
          this.loadDirecciones();
        },
        error: (err) => {
          alert('Error al eliminar la dirección');
          console.error(err);
        }
      });
    }
  }

  agregarDireccion(): void {
    // ⭐ Validar límite de 5 direcciones
    if (this.direcciones.length >= 5) {
      alert('Has alcanzado el límite máximo de 5 direcciones. Elimina una dirección existente antes de agregar una nueva.');
      return;
    }
    this.router.navigate(['/restaurante/direcciones/nueva']);
  }
}
