import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DeliveryService, Entrega, PedidoCompleto } from './delivery.service';
import { AuthService } from '../edge/auth.service';

@Component({
  selector: 'app-delivery-entregas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entregas.component.html'
})
export class DeliveryEntregasComponent implements OnInit {
  entregas: Entrega[] = [];
  entregasEnCamino: Entrega[] = [];
  entregasEntregadas: Entrega[] = [];
  filtroActual: 'en_camino' | 'entregadas' = 'en_camino';
  
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  userName: string = '';
  usuarioId: string = '';
  
  pedidoSeleccionado: PedidoCompleto | null = null;
  mostrarDetalles = false;
  cargandoDetalles = false;

  // Propiedades para el modal de confirmación
  mostrarModalConfirmacion = false;
  entregaSeleccionada: Entrega | null = null;
  codigoEntrega = '';
  comentariosEntrega = '';

  // Constantes para usar en el template HTML
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

      // ⭐ Usar AuthService para verificar autenticación (usa la clave correcta del token)
      if (!this.authService.isLoggedIn()) {
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
        console.log('✅ Entregas cargadas desde servidor:', entregas);
        
        this.entregas = entregas;
        
        // Separar por estado
        this.entregasEnCamino = entregas.filter(e => e.estado !== 'ENTREGADO');
        this.entregasEntregadas = entregas.filter(e => e.estado === 'ENTREGADO');
        
        // Log para debug
        console.log(`📊 Entregadas: ${this.entregasEntregadas.length}, En camino: ${this.entregasEnCamino.length}`);
        
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar entregas';
        console.error('❌ Error al cargar entregas:', err);
        this.loading = false;
      }
    });
  }

  // Cambiar filtro
  cambiarFiltro(filtro: 'en_camino' | 'entregadas'): void {
    this.filtroActual = filtro;
  }

  // Obtener entregas según filtro
  get entregasFiltradas(): Entrega[] {
    return this.filtroActual === 'en_camino' ? this.entregasEnCamino : this.entregasEntregadas;
  }

  // Calcular duración de entrega
  calcularDuracion(entrega: Entrega): string {
    if (entrega.estado !== 'ENTREGADO' || !entrega.fechaCreacion || !entrega.fechaActualizacion) {
      return 'N/A';
    }

    const inicio = new Date(entrega.fechaCreacion);
    const fin = new Date(entrega.fechaActualizacion);
    const diferenciaMs = fin.getTime() - inicio.getTime();
    const minutos = Math.floor(diferenciaMs / 60000);

    if (minutos < 60) {
      return `${minutos} min`;
    } else {
      const horas = Math.floor(minutos / 60);
      const minutosRestantes = minutos % 60;
      return `${horas}h ${minutosRestantes}min`;
    }
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

  abrirModalConfirmarEntrega(entrega: Entrega): void {
    console.log('🔵 Abriendo modal para entrega:', entrega);
    console.log('🔵 pedidoId:', entrega.pedidoId);
    console.log('🔵 estado actual:', entrega.estado);
    
    this.entregaSeleccionada = entrega;
    this.mostrarModalConfirmacion = true;
    this.codigoEntrega = '';
    this.comentariosEntrega = '';
    this.errorMessage = null;
  }

  cerrarModalConfirmacion(): void {
    this.mostrarModalConfirmacion = false;
    this.entregaSeleccionada = null;
    this.codigoEntrega = '';
    this.comentariosEntrega = '';
  }

  confirmarEntregaFinal(): void {
    if (!this.codigoEntrega.trim()) {
      this.errorMessage = 'El código de confirmación es obligatorio';
      return;
    }

    if (!this.entregaSeleccionada) {
      this.errorMessage = 'No hay entrega seleccionada';
      return;
    }

    const pedidoId = this.entregaSeleccionada.pedidoId || this.entregaSeleccionada.id;

    if (!pedidoId) {
      console.error('❌ Entrega sin pedidoId:', this.entregaSeleccionada);
      this.errorMessage = 'Error: No se encontró el ID del pedido';
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    console.log('🟢 Confirmando entrega con datos:', {
      pedidoId: pedidoId,
      codigoEntrega: this.codigoEntrega.trim(),
      comentarios: this.comentariosEntrega.trim() || '',
      entregaActual: this.entregaSeleccionada
    });

    this.deliveryService.confirmarEntrega({
      pedidoId: pedidoId,
      codigoEntrega: this.codigoEntrega.trim(),
      comentarios: this.comentariosEntrega.trim() || ''
    }).subscribe({
      next: (entregaActualizada) => {
        console.log('✅ Entrega confirmada exitosamente:', entregaActualizada);
        console.log('✅ Estado recibido del backend:', entregaActualizada.estado);
        
        // Actualizar el estado local inmediatamente
        const index = this.entregas.findIndex(e => e.id === this.entregaSeleccionada?.id);
        if (index !== -1) {
          this.entregas[index].estado = 'ENTREGADO';
          this.entregas[index].fechaActualizacion = new Date().toISOString();
          console.log('✅ Estado actualizado localmente para entrega:', this.entregas[index].id);
        }

        // Re-filtrar las entregas
        this.entregasEnCamino = this.entregas.filter(e => e.estado !== 'ENTREGADO');
        this.entregasEntregadas = this.entregas.filter(e => e.estado === 'ENTREGADO');
        
        console.log(`📊 Después de confirmar - Entregadas: ${this.entregasEntregadas.length}, En camino: ${this.entregasEnCamino.length}`);

        // Cambiar al filtro de entregadas automáticamente
        this.filtroActual = 'entregadas';

        this.successMessage = 'Entrega confirmada correctamente';
        this.cerrarModalConfirmacion();
        
        // Recargar desde el servidor después de un breve delay
        setTimeout(() => {
          console.log('🔄 Recargando entregas desde el servidor...');
          this.cargarEntregas();
          this.successMessage = null;
        }, 1000);
        
        this.loading = false;
      },
      error: (err: any) => {
        console.error('❌ Error al confirmar entrega:', err);
        this.errorMessage = err?.error?.message || 'Error al confirmar entrega';
        this.loading = false;
      }
    });
  }

  actualizarEstado(
    entrega: Entrega, 
    nuevoEstado: 'EN_CAMINO_RECOGIDO' | 'EN_CAMINO_HACIA_CLIENTE' | 'ENTREGADO' | 'CON_EL_REPARTIDOR'
  ): void {
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
      'CON_EL_REPARTIDOR': 'Con el Repartidor',
      'EN_CAMINO': 'En Camino'
    };
    return estados[estado] || estado;
  }

  getEstadoColor(estado: string): string {
    const colores: { [key: string]: string } = {
      'EN_CAMINO_RECOGIDO': 'bg-blue-100 text-blue-800',
      'EN_CAMINO_HACIA_CLIENTE': 'bg-yellow-100 text-yellow-800',
      'ENTREGADO': 'bg-green-100 text-green-800',
      'CON_EL_REPARTIDOR': 'bg-purple-100 text-purple-800',
      'EN_CAMINO': 'bg-orange-100 text-orange-800'
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
