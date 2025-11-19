import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { DeliveryService, Entrega, PedidoCompleto } from './delivery.service';
import { AuthService } from '../edge/auth.service';

@Component({
  selector: 'app-delivery-entregas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './entregas.component.html'
})
export class DeliveryEntregasComponent implements OnInit {
  entregas: Entrega[] = [];
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  userName: string = '';
  usuarioId: string = '';
  
  pedidoSeleccionado: PedidoCompleto | null = null;
  mostrarDetalles = false;
  cargandoDetalles = false;

  // ✅ AGREGAR ESTAS CONSTANTES para usar en el template HTML
  readonly ESTADO_EN_CAMINO_RECOGIDO = 'EN_CAMINO_RECOGIDO';
  readonly ESTADO_EN_CAMINO_HACIA_CLIENTE = 'EN_CAMINO_HACIA_CLIENTE';
  readonly ESTADO_ENTREGADO = 'ENTREGADO';
  readonly ESTADO_CON_EL_REPARTIDOR = 'CON_EL_REPARTIDOR';

  private platformId = inject(PLATFORM_ID);

  constructor(
    private deliveryService: DeliveryService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.usuarioId = this.authService.getUserId() || '';
      this.userName = this.authService.getUserName() || '';

      if (!this.authService.isRepartidor()) {
        this.router.navigate(['/login']);
        return;
      }

      if (!localStorage.getItem('token')) {
        this.router.navigate(['/login']);
        return;
      }

      this.cargarEntregas();
    }
  }

  cargarEntregas(): void {
    this.loading = true;
    this.errorMessage = null;

    this.deliveryService.listarEntregas(this.usuarioId).subscribe({
      next: (entregas) => {
        this.entregas = entregas;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar entregas';
        console.error(err);
        this.loading = false;
      }
    });
  }

  verDetallesPedido(pedidoId: string): void {
    this.cargandoDetalles = true;
    this.errorMessage = null;

    this.deliveryService.obtenerPedidoCompleto(pedidoId).subscribe({
      next: (pedido) => {
        this.pedidoSeleccionado = pedido;
        this.mostrarDetalles = true;
        this.cargandoDetalles = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar detalles del pedido';
        console.error(err);
        this.cargandoDetalles = false;
      }
    });
  }

  cerrarDetalles(): void {
    this.mostrarDetalles = false;
    this.pedidoSeleccionado = null;
  }

  actualizarEstado(entrega: Entrega, nuevoEstado: 'EN_CAMINO_RECOGIDO' | 'EN_CAMINO_HACIA_CLIENTE' | 'ENTREGADO'): void {
    if (!confirm(`¿Cambiar estado a ${this.getEstadoTexto(nuevoEstado)}?`)) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.deliveryService.actualizarEstadoEntrega(entrega.id, nuevoEstado).subscribe({
      next: () => {
        this.successMessage = 'Estado actualizado exitosamente';
        this.cargarEntregas();
        setTimeout(() => this.successMessage = null, 3000);
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al actualizar estado';
        console.error(err);
        this.loading = false;
      }
    });
  }

  getEstadoTexto(estado: string): string {
    const estados: { [key: string]: string } = {
      'EN_CAMINO_RECOGIDO': 'En Camino a Recoger',
      'EN_CAMINO_HACIA_CLIENTE': 'En Camino al Cliente',
      'ENTREGADO': 'Entregado',
      'CON_EL_REPARTIDOR': 'Con el Repartidor'
    };
    return estados[estado] || estado;
  }

  getEstadoColor(estado: string): string {
    const colores: { [key: string]: string } = {
      'EN_CAMINO_RECOGIDO': 'bg-blue-100 text-blue-800',
      'EN_CAMINO_HACIA_CLIENTE': 'bg-yellow-100 text-yellow-800',
      'ENTREGADO': 'bg-green-100 text-green-800',
      'CON_EL_REPARTIDOR': 'bg-purple-100 text-purple-800'
    };
    return colores[estado] || 'bg-gray-100 text-gray-800';
  }

  volver(): void {
    this.router.navigate(['/delivery/main']);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
