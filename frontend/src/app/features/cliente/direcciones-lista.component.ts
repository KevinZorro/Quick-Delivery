import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { DireccionService, Direccion } from './direccion.service';
import { HeaderComponent } from './header.component';

@Component({
  selector: 'app-direcciones-lista',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './direcciones-lista.component.html'
})
export class DireccionesListaComponent implements OnInit {
  direcciones: Direccion[] = [];
  loading = true;
  errorMessage: string | null = null;
  usuarioId: string = '';

  constructor(
    private direccionService: DireccionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // ⭐ Obtener el userId del localStorage
    const clienteId = localStorage.getItem('quick-delivery-userId');
    
    if (!clienteId) {
      this.errorMessage = 'Error: No se pudo identificar el usuario. Por favor inicia sesión nuevamente.';
      this.loading = false;
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      return;
    }

    this.usuarioId = clienteId;
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
    this.router.navigate(['/cliente/direcciones/editar', id]);
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
    this.router.navigate(['/cliente/direcciones/nueva']);
  }
}
