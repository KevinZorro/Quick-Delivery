import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DeliveryService, Entrega, PedidoCompleto, ResenasResponse, Opinion } from './delivery.service';
import { AuthService } from '../edge/auth.service';

@Component({
  selector: 'app-delivery-entregas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entregas.component.html'
})
export class DeliveryEntregasComponent implements OnInit {
  // Entregas
  entregas: Entrega[] = [];
  entregasEnCamino: Entrega[] = [];
  entregasEntregadas: Entrega[] = [];
  filtroActual: 'en_camino' | 'entregadas' = 'en_camino';

  // Filtros de fecha (se usan para ambos estados)
  filtroFechaDesde: string = '';
  filtroFechaHasta: string = '';
  // Orden solo para ENTREGADAS
  ordenEntregadas: 'mas_rapidas' | 'mas_lentas' | 'reciente' | 'antiguo' = 'reciente';

  // Estados de carga y mensajes
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  userName: string = '';
  usuarioId: string = '';

  // Detalles del pedido
  pedidoSeleccionado: PedidoCompleto | null = null;
  mostrarDetalles = false;
  cargandoDetalles = false;

  // Modal de confirmación de entrega
  mostrarModalConfirmacion = false;
  entregaSeleccionada: Entrega | null = null;
  codigoEntrega = '';
  comentariosEntrega = '';

  // Reseñas
  mostrarModalResenas = false;
  cargandoResenas = false;
  misResenas: Opinion[] = [];
  promedioCalificacion = 0;
  repartidorId: string | null = null;

  // Constantes estados
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

      if (!this.authService.isRepartidor() || !this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }

      this.cargarEntregas();
    }
  }

  // =========================
  // ENTREGAS
  // =========================

  cargarEntregas(): void {
    this.loading = true;
    this.errorMessage = null;

    this.deliveryService.listarEntregas(this.usuarioId).subscribe({
      next: (entregas) => {
        console.log('✅ Entregas cargadas desde servidor:', entregas);

        this.entregas = entregas;
        this.entregasEnCamino = entregas.filter(e => e.estado !== 'ENTREGADO');
        this.entregasEntregadas = entregas.filter(e => e.estado === 'ENTREGADO');

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

  cambiarFiltro(filtro: 'en_camino' | 'entregadas'): void {
    this.filtroActual = filtro;
  }

  get entregasFiltradas(): Entrega[] {
    let lista = this.filtroActual === 'en_camino'
      ? [...this.entregasEnCamino]
      : [...this.entregasEntregadas];

    // Filtro de fechas para ambos estados
    if (this.filtroFechaDesde || this.filtroFechaHasta) {
      const desde = this.filtroFechaDesde ? new Date(this.filtroFechaDesde) : null;
      const hasta = this.filtroFechaHasta ? new Date(this.filtroFechaHasta) : null;

      lista = lista.filter(e => {
        // Para en_camino usamos fechaCreacion, para entregadas fechaActualizacion
        const fechaBase = this.filtroActual === 'en_camino'
          ? e.fechaCreacion
          : (e.fechaActualizacion || e.fechaCreacion);

        if (!fechaBase) return false;
        const fecha = new Date(fechaBase);

        if (desde && fecha < desde) {
          return false;
        }
        if (hasta) {
          const hastaInclusive = new Date(hasta);
          hastaInclusive.setDate(hastaInclusive.getDate() + 1);
          if (fecha >= hastaInclusive) {
            return false;
          }
        }
        return true;
      });
    }

    // Orden extra solo para ENTREGADAS (por fecha o tiempo de entrega)
    if (this.filtroActual === 'entregadas') {
      lista.sort((a, b) => {
        const fa = a.fechaActualizacion ? new Date(a.fechaActualizacion).getTime() : 0;
        const fb = b.fechaActualizacion ? new Date(b.fechaActualizacion).getTime() : 0;

        const durA = this.getDuracionMinutos(a);
        const durB = this.getDuracionMinutos(b);

        if (this.ordenEntregadas === 'reciente') {
          return fb - fa;
        } else if (this.ordenEntregadas === 'antiguo') {
          return fa - fb;
        } else if (this.ordenEntregadas === 'mas_rapidas') {
          return durA - durB;
        } else if (this.ordenEntregadas === 'mas_lentas') {
          return durB - durA;
        }
        return 0;
      });
    }

    return lista;
  }

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

  private getDuracionMinutos(entrega: Entrega): number {
    if (entrega.estado !== 'ENTREGADO' || !entrega.fechaCreacion || !entrega.fechaActualizacion) {
      return Number.MAX_SAFE_INTEGER;
    }
    const inicio = new Date(entrega.fechaCreacion);
    const fin = new Date(entrega.fechaActualizacion);
    const diferenciaMs = fin.getTime() - inicio.getTime();
    return Math.max(0, Math.floor(diferenciaMs / 60000));
  }

  // =========================
  // DETALLES
  // =========================

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

  // =========================
  // CONFIRMAR ENTREGA
  // =========================

  abrirModalConfirmarEntrega(entrega: Entrega): void {
    console.log('🔵 Abriendo modal para entrega:', entrega);

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
      this.errorMessage = 'Error: No se encontró el ID del pedido';
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    this.deliveryService.confirmarEntrega({
      pedidoId: pedidoId,
      codigoEntrega: this.codigoEntrega.trim(),
      comentarios: this.comentariosEntrega.trim() || ''
    }).subscribe({
      next: () => {
        console.log('✅ Entrega confirmada exitosamente');

        const index = this.entregas.findIndex(e => e.id === this.entregaSeleccionada?.id);
        if (index !== -1) {
          this.entregas[index].estado = 'ENTREGADO';
          this.entregas[index].fechaActualizacion = new Date().toISOString();
        }

        this.entregasEnCamino = this.entregas.filter(e => e.estado !== 'ENTREGADO');
        this.entregasEntregadas = this.entregas.filter(e => e.estado === 'ENTREGADO');

        this.filtroActual = 'entregadas';
        this.successMessage = 'Entrega confirmada correctamente';
        this.cerrarModalConfirmacion();

        setTimeout(() => {
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

  // =========================
  // RESEÑAS
  // =========================

  verMisResenas(): void {
    this.mostrarModalResenas = true;
    this.cargandoResenas = true;
    this.errorMessage = null;

    if (this.repartidorId) {
      this.cargarDatosResenas(this.repartidorId);
    } else {
      this.deliveryService.obtenerRepartidorId(this.usuarioId).subscribe({
        next: (response) => {
          this.repartidorId = response.repartidorId;
          this.cargarDatosResenas(this.repartidorId);
        },
        error: (err) => {
          console.error('Error al obtener ID repartidor', err);
          this.errorMessage = 'No se pudo identificar tu perfil de repartidor.';
          this.cargandoResenas = false;
        }
      });
    }
  }

  private cargarDatosResenas(id: string): void {
    this.deliveryService.obtenerResenas(id).subscribe({
      next: (data: ResenasResponse) => {
        this.misResenas = data.opiniones;
        this.promedioCalificacion = data.promedio;
        this.cargandoResenas = false;
      },
      error: (err) => {
        console.error('Error cargando reseñas', err);
        this.errorMessage = 'Error al cargar tus calificaciones.';
        this.cargandoResenas = false;
      }
    });
  }

  cerrarModalResenas(): void {
    this.mostrarModalResenas = false;
  }

  getEstrellas(calificacion: number): number[] {
    const entero = Math.round(calificacion);
    return Array(5).fill(0).map((_, i) => i < entero ? 1 : 0);
  }

  // =========================
  // UTILIDADES
  // =========================

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