import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RestauranteService, Restaurante } from '../shared/services/restaurante.service';
import { ProductoService, Producto } from '../shared/services/producto.service';
import { AuthService } from '../../edge/auth.service';
import { ProductoFormComponent } from './producto-form.component';

@Component({
  selector: 'app-restaurante-main',
  standalone: true,
  imports: [CommonModule, ProductoFormComponent],
  templateUrl: './restaurante-main.component.html'
})
export class RestauranteMainComponent implements OnInit {
  restaurante: Restaurante | null = null;
  productos: Producto[] = [];
  loading = true;
  errorMessage: string | null = null;
  usuarioId: string = '';
  mostrarFormProducto = false;
  productoSeleccionado: Producto | null = null;

  constructor(
    private restauranteService: RestauranteService,
    private productoService: ProductoService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = localStorage.getItem('quick-delivery-userId');
    
    if (!userId) {
      this.errorMessage = 'Error: No se pudo identificar el usuario. Por favor inicia sesión nuevamente.';
      this.loading = false;
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      return;
    }

    this.usuarioId = userId;
    this.cargarRestaurante();
  }

  cargarRestaurante(): void {
    this.loading = true;
    this.restauranteService.obtenerPorUsuarioId(this.usuarioId).subscribe({
      next: (restaurante) => {
        this.restaurante = restaurante;
        this.cargarProductos();
      },
      error: (err) => {
        if (err.status === 404) {
          this.errorMessage = 'No tienes un restaurante registrado';
        } else {
          this.errorMessage = 'Error al cargar el restaurante';
        }
        this.loading = false;
        console.error(err);
      }
    });
  }

  cargarProductos(): void {
    if (this.restaurante) {
      this.productoService.listarPorRestaurante(this.restaurante.id).subscribe({
        next: (productos) => {
          this.productos = productos;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error al cargar productos:', err);
          this.loading = false;
        }
      });
    }
  }

  irAEditarRestaurante(): void {
    this.router.navigate(['/restaurante/editar']);
  }

  irARegistroRestaurante(): void {
    this.router.navigate(['/registro-restaurante']);
  }

  abrirFormularioProducto(producto?: Producto): void {
    this.productoSeleccionado = producto || null;
    this.mostrarFormProducto = true;
  }

  cerrarFormularioProducto(): void {
    this.mostrarFormProducto = false;
    this.productoSeleccionado = null;
  }

  eliminarProducto(id: string): void {
    if (confirm('¿Estás seguro de que deseas eliminar este producto?')) {
      this.productoService.eliminar(id).subscribe({
        next: () => {
          this.cargarProductos();
        },
        error: (err) => {
          alert('Error al eliminar el producto');
          console.error(err);
        }
      });
    }
  }

  cambiarDisponibilidad(producto: Producto): void {
    if (producto.id) {
      this.productoService.cambiarDisponibilidad(producto.id, !producto.disponible).subscribe({
        next: () => {
          producto.disponible = !producto.disponible;
        },
        error: (err) => {
          alert('Error al cambiar disponibilidad');
          console.error(err);
        }
      });
    }
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('quick-delivery-userId');
    localStorage.removeItem('quick-delivery-userName');
    localStorage.removeItem('quick-delivery-userRole');
    
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
