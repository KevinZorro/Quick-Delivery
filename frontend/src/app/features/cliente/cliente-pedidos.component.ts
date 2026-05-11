import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService, Pedido } from './pedido.service';
import { RestauranteService } from './restaurante.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from './header.component';

interface PedidoDetallado extends Pedido {
  fechaFormateada?: string;
  estadoColor?: string;
  estadoTexto?: string;
  restauranteCalificado?: boolean;
  repartidorCalificado?: boolean;
}

@Component({
  selector: 'app-cliente-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent],
  templateUrl: './cliente-pedidos.component.html'
})
export class ClientePedidosComponent implements OnInit {

  pedidos: PedidoDetallado[] = [];
  pedidosFiltrados: PedidoDetallado[] = [];

  loading: boolean = true;
  error: string = '';

  filtroEstado: string = 'TODOS';
  ordenamiento: string = 'reciente';

  filtroFechaDesde: string = '';
  filtroFechaHasta: string = '';

  // Modal detalles
  mostrarModal: boolean = false;
  pedidoSeleccionado: PedidoDetallado | null = null;
  loadingDetalles: boolean = false;

  // Modal calificación
  mostrarModalCalificacion: boolean = false;
  pedidoParaCalificar: PedidoDetallado | null = null;

  // Restaurante
  calificacionRestaurante: number = 0;
  comentarioRestaurante: string = '';
  enviandoCalificacionRestaurante: boolean = false;

  // Repartidor
  calificacionRepartidor: number = 0;
  comentarioRepartidor: string = '';
  enviandoCalificacionRepartidor: boolean = false;

  // Toast notificación
  toastVisible: boolean = false;
  toastMensaje: string = '';
  toastTipo: 'exito' | 'error' = 'exito';
  private toastTimeout: any;

  estados = [
    { valor: 'TODOS', texto: 'Todos los pedidos' },
    { valor: 'NUEVO', texto: 'Nuevo' },
    { valor: 'ACEPTADO', texto: 'Aceptado' },
    { valor: 'EN_COCINA', texto: 'En cocina' },
    { valor: 'CON_EL_REPARTIDOR', texto: 'Con repartidor' },
    { valor: 'ENTREGADO', texto: 'Entregado' },
    { valor: 'RECHAZADO_POR_RESTAURANTE', texto: 'Rechazado' },
    { valor: 'INICIADO', texto: 'Iniciados' }, // Retrocompatibilidad
    { valor: 'EN_COCINA', texto: 'En cocina' },
    { valor: 'CON_EL_REPARTIDOR', texto: 'Con el repartidor' },
    { valor: 'ENTREGADO', texto: 'Entregados' }
  ];

  constructor(
    private pedidoService: PedidoService,
    private restauranteService: RestauranteService,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.cargarPedidos();
  }

  mostrarToast(mensaje: string, tipo: 'exito' | 'error' = 'exito'): void {
    if (this.toastTimeout) clearTimeout(this.toastTimeout);
    this.toastMensaje = mensaje;
    this.toastTipo = tipo;
    this.toastVisible = true;
    this.toastTimeout = setTimeout(() => {
      this.toastVisible = false;
    }, 3500);
  }

  cargarPedidos(): void {
    this.loading = true;
    this.error = '';

    const usuarioId = localStorage.getItem('quick-delivery-userId');

    if (!usuarioId) {
      this.error = 'No se pudo identificar al usuario.';
      this.loading = false;
      return;
    }

    this.pedidoService.listarPedidosUsuario(usuarioId).subscribe({
      next: (pedidos) => {
        this.pedidos = pedidos.map(pedido => {
          const restauranteCalificado =
            localStorage.getItem(`restaurante-${pedido.id}`) === 'true';
          const repartidorCalificado =
            localStorage.getItem(`repartidor-${pedido.id}`) === 'true';

          return {
            ...pedido,
            fechaFormateada: this.formatearFecha(pedido.fechaCreacion),
            estadoColor: this.obtenerColorEstado(pedido.estado),
            estadoTexto: this.obtenerTextoEstado(pedido.estado),
            restauranteCalificado,
            repartidorCalificado
          };
        });

        this.aplicarFiltros();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar pedidos:', error);
        this.error = 'No se pudieron cargar los pedidos.';
        this.loading = false;
      }
    });
  }

  aplicarFiltros(): void {
    if (this.filtroEstado === 'TODOS') {
      this.pedidosFiltrados = [...this.pedidos];
    } else {
      this.pedidosFiltrados = this.pedidos.filter(
        pedido => pedido.estado === this.filtroEstado
      );
    }

    if (this.filtroFechaDesde || this.filtroFechaHasta) {
      const desde = this.filtroFechaDesde ? new Date(this.filtroFechaDesde) : null;
      const hasta = this.filtroFechaHasta ? new Date(this.filtroFechaHasta) : null;

      this.pedidosFiltrados = this.pedidosFiltrados.filter(p => {
        const fecha = new Date(p.fechaCreacion);
        if (desde && fecha < desde) return false;
        if (hasta) {
          const hastaInclusive = new Date(hasta);
          hastaInclusive.setDate(hastaInclusive.getDate() + 1);
          if (fecha >= hastaInclusive) return false;
        }
        return true;
      });
    }

    if (this.ordenamiento === 'reciente') {
      this.pedidosFiltrados.sort(
        (a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
      );
    } else if (this.ordenamiento === 'antiguo') {
      this.pedidosFiltrados.sort(
        (a, b) => new Date(a.fechaCreacion).getTime() - new Date(b.fechaCreacion).getTime()
      );
    } else if (this.ordenamiento === 'mayor') {
      this.pedidosFiltrados.sort((a, b) => b.total - a.total);
    } else if (this.ordenamiento === 'menor') {
      this.pedidosFiltrados.sort((a, b) => a.total - b.total);
    }
  }

  abrirModalCalificacion(pedido: PedidoDetallado, event: Event): void {
    event.stopPropagation();

    if (pedido.restauranteCalificado && pedido.repartidorCalificado) {
      alert('Este pedido ya fue calificado completamente.');
      return;
    }

    this.pedidoParaCalificar = pedido;

    this.calificacionRestaurante = 0;
    this.comentarioRestaurante = '';
    this.enviandoCalificacionRestaurante = false;

    this.calificacionRepartidor = 0;
    this.comentarioRepartidor = '';
    this.enviandoCalificacionRepartidor = false;

    this.mostrarModalCalificacion = true;
    document.body.style.overflow = 'hidden';
  }

  cerrarModalCalificacion(): void {
    this.mostrarModalCalificacion = false;
    this.pedidoParaCalificar = null;
    document.body.style.overflow = 'auto';
  }

  setCalificacionRestaurante(stars: number): void {
    if (this.pedidoParaCalificar?.restauranteCalificado) return;
    this.calificacionRestaurante = stars;
  }

  setCalificacionRepartidor(stars: number): void {
    if (this.pedidoParaCalificar?.repartidorCalificado) return;
    this.calificacionRepartidor = stars;
  }

  enviarCalificacionRestaurante(): void {
    if (
      !this.pedidoParaCalificar ||
      this.calificacionRestaurante === 0 ||
      this.pedidoParaCalificar.restauranteCalificado
    ) return;

    this.enviandoCalificacionRestaurante = true;

    this.pedidoService
      .calificarRestaurante(
        this.pedidoParaCalificar.id,
        this.calificacionRestaurante,
        this.comentarioRestaurante
      )
      .subscribe({
        next: () => {
          if (this.pedidoParaCalificar) {
            this.pedidoParaCalificar.restauranteCalificado = true;
            localStorage.setItem(`restaurante-${this.pedidoParaCalificar.id}`, 'true');

            const pedidoEnLista = this.pedidos.find(p => p.id === this.pedidoParaCalificar!.id);
            if (pedidoEnLista) pedidoEnLista.restauranteCalificado = true;
          }

          this.mostrarToast('✅ ¡Gracias! Calificación del restaurante enviada.');

          this.calificacionRestaurante = 0;
          this.comentarioRestaurante = '';
          this.enviandoCalificacionRestaurante = false;

          this.checkCerrarModal();
        },
        error: (err) => {
          console.error(err);
          // Sin notificación: el botón cambia visualmente a "ya calificado"
          this.enviandoCalificacionRestaurante = false;

          if (this.pedidoParaCalificar) {
            this.pedidoParaCalificar.restauranteCalificado = true;
            localStorage.setItem(`restaurante-${this.pedidoParaCalificar.id}`, 'true');

            const pedidoEnLista = this.pedidos.find(p => p.id === this.pedidoParaCalificar!.id);
            if (pedidoEnLista) pedidoEnLista.restauranteCalificado = true;
          }
        }
      });
  }

  enviarCalificacionRepartidor(): void {
    if (
      !this.pedidoParaCalificar ||
      this.calificacionRepartidor === 0 ||
      this.pedidoParaCalificar.repartidorCalificado
    ) return;

    this.enviandoCalificacionRepartidor = true;

    this.pedidoService
      .calificarRepartidor(
        this.pedidoParaCalificar.id,
        this.calificacionRepartidor,
        this.comentarioRepartidor
      )
      .subscribe({
        next: () => {
          if (this.pedidoParaCalificar) {
            this.pedidoParaCalificar.repartidorCalificado = true;
            localStorage.setItem(`repartidor-${this.pedidoParaCalificar.id}`, 'true');

            const pedidoEnLista = this.pedidos.find(p => p.id === this.pedidoParaCalificar!.id);
            if (pedidoEnLista) pedidoEnLista.repartidorCalificado = true;
          }

          this.mostrarToast('✅ ¡Gracias! Calificación del repartidor enviada.');

          this.calificacionRepartidor = 0;
          this.comentarioRepartidor = '';
          this.enviandoCalificacionRepartidor = false;

          this.checkCerrarModal();
        },
        error: (err) => {
          console.error(err);
          this.mostrarToast('⚠️ El repartidor ya fue calificado anteriormente.', 'error');
          this.enviandoCalificacionRepartidor = false;

          if (this.pedidoParaCalificar) {
            this.pedidoParaCalificar.repartidorCalificado = true;
            localStorage.setItem(`repartidor-${this.pedidoParaCalificar.id}`, 'true');

            const pedidoEnLista = this.pedidos.find(p => p.id === this.pedidoParaCalificar!.id);
            if (pedidoEnLista) pedidoEnLista.repartidorCalificado = true;
          }
        }
      });
  }

  checkCerrarModal(): void {
    if (!this.pedidoParaCalificar) return;

    const ambosCaificados =
      this.pedidoParaCalificar.restauranteCalificado &&
      this.pedidoParaCalificar.repartidorCalificado;

    if (ambosCaificados) {
      setTimeout(() => {
        this.cerrarModalCalificacion();
      }, 1500);
    }
  }

  verDetallePedido(pedidoId: string): void {
    const pedido = this.pedidos.find(p => p.id === pedidoId);
    if (!pedido) return;

    this.pedidoSeleccionado = pedido;
    this.mostrarModal = true;
    document.body.style.overflow = 'hidden';
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.pedidoSeleccionado = null;
    document.body.style.overflow = 'auto';
  }

  obtenerCodigoEntrega(pedidoId: string): void {
    alert(`Código para pedido ${pedidoId}`);
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatearId(id: string | undefined | null): string {
    if (!id) return 'N/A';
    return id.substring(0, 8);
  }

  obtenerColorEstado(estado: string): string {
    const colores: { [key: string]: string } = {
      'NUEVO': 'bg-blue-100 text-blue-800 border-blue-300',
      'ACEPTADO': 'bg-yellow-100 text-yellow-800 border-yellow-300',
      'EN_COCINA': 'bg-orange-100 text-orange-800 border-orange-300',
      'CON_EL_REPARTIDOR': 'bg-purple-100 text-purple-800 border-purple-300',
      'ENTREGADO': 'bg-green-100 text-green-800 border-green-300',
      'RECHAZADO_POR_RESTAURANTE': 'bg-red-100 text-red-800 border-red-300',
      'INICIADO': 'bg-yellow-100 text-yellow-800 border-yellow-300' // Retrocompatibilidad
    };
    return colores[estado] || 'bg-gray-100 text-gray-800 border-gray-300';
  }

  obtenerTextoEstado(estado: string): string {
    const textos: { [key: string]: string } = {
      'NUEVO': 'Nuevo',
      'ACEPTADO': 'Aceptado',
      'EN_COCINA': 'En cocina',
      'CON_EL_REPARTIDOR': 'Con el repartidor',
      'ENTREGADO': 'Entregado',
      'RECHAZADO_POR_RESTAURANTE': 'Rechazado',
      'INICIADO': 'Iniciado' // Retrocompatibilidad
    };
    return textos[estado] || estado;
  }

  obtenerIconoEstado(estado: string): string {
    const iconos: { [key: string]: string } = {
      'NUEVO': '📋',
      'ACEPTADO': '✓',
      'EN_COCINA': '👨‍🍳',
      'CON_EL_REPARTIDOR': '🚴',
      'ENTREGADO': '✅',
      'RECHAZADO_POR_RESTAURANTE': '❌',
      'INICIADO': '⏳' // Retrocompatibilidad
    };
    return iconos[estado] || '📦';
  }

  formatearPrecio(precio: number): string {
    if (isNaN(precio) || precio === null || precio === undefined) {
      return 'Precio no disponible';
    }
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(precio);
  }

  calcularCantidadTotal(): number {
    if (!this.pedidoSeleccionado?.items) return 0;
    return this.pedidoSeleccionado.items.reduce(
      (total, item) => total + item.cantidad, 0
    );
  }
}