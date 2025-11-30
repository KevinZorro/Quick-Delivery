import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../edge/auth.service';
import { PedidoService, Pedido } from './pedido.service';

@Component({
  selector: 'app-restaurante-pedidos-pendientes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedidos-pendientes.component.html'
})
export class RestaurantePedidosPendientesComponent implements OnInit {
  pedidos: Pedido[] = [];
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  restauranteNombre: string = '';

  private platformId = inject(PLATFORM_ID);

  constructor(
    private pedidoService: PedidoService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.restauranteNombre = localStorage.getItem('quick-delivery-userName') || '';

      if (!this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }

      this.cargarPedidos();
    }
  }

  cargarPedidos(): void {
    this.loading = true;
    this.errorMessage = null;

    this.pedidoService.obtenerPedidosPendientes().subscribe({
      next: (pedidos) => {
        this.pedidos = pedidos;
        this.loading = false;
        console.log('Pedidos cargados:', pedidos.length);
      },
      error: (err) => {
        console.error('Error al cargar pedidos:', err);
        this.errorMessage = err.error?.message || 'Error al cargar los pedidos pendientes';
        this.loading = false;
      }
    });
  }

  aceptarPedido(pedido: Pedido): void {
    if (!confirm(`¿Estás seguro de aceptar el pedido #${pedido.id.substring(0, 8)}?`)) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.pedidoService.aceptarPedido(pedido.id).subscribe({
      next: () => {
        this.successMessage = 'Pedido aceptado exitosamente';
        this.cargarPedidos();
        setTimeout(() => this.successMessage = null, 3000);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al aceptar pedido:', err);
        this.errorMessage = err.error?.message || 'Error al aceptar el pedido';
        this.loading = false;
      }
    });
  }

  rechazarPedido(pedido: Pedido): void {
    if (!confirm(`¿Estás seguro de rechazar el pedido #${pedido.id.substring(0, 8)}?`)) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.pedidoService.rechazarPedido(pedido.id).subscribe({
      next: () => {
        this.successMessage = 'Pedido rechazado exitosamente';
        this.cargarPedidos();
        setTimeout(() => this.successMessage = null, 3000);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al rechazar pedido:', err);
        this.errorMessage = err.error?.message || 'Error al rechazar el pedido';
        this.loading = false;
      }
    });
  }

  formatearFecha(fecha: string): string {
    if (!fecha) return 'N/A';
    try {
      return new Date(fecha).toLocaleString('es-ES');
    } catch {
      return fecha;
    }
  }

  calcularTotalItems(pedido: Pedido): number {
    if (!pedido.items || pedido.items.length === 0) {
      return 0;
    }
    return pedido.items.reduce((sum, item) => sum + item.cantidad, 0);
  }

  volver(): void {
    this.router.navigate(['/restaurante/main']);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

