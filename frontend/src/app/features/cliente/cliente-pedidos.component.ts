import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService, Pedido } from './pedido.service';
import { RestauranteService } from './restaurante.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from './header.component';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

interface PedidoDetallado extends Pedido {
  fechaFormateada?: string;
  estadoColor?: string;
  estadoTexto?: string;
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

  // Modal Detalles
  mostrarModal: boolean = false;
  pedidoSeleccionado: PedidoDetallado | null = null;
  loadingDetalles: boolean = false;

  // ⭐ Modal Calificación
  mostrarModalCalificacion: boolean = false;
  pedidoParaCalificar: PedidoDetallado | null = null;
  
  // Estado calificación
  calificacionRestaurante: number = 0;
  comentarioRestaurante: string = '';
  enviandoCalificacionRestaurante: boolean = false;
  
  calificacionRepartidor: number = 0;
  comentarioRepartidor: string = '';
  enviandoCalificacionRepartidor: boolean = false;

  estados = [
    { valor: 'TODOS', texto: 'Todos los pedidos' },
    { valor: 'INICIADO', texto: 'Iniciados' },
    { valor: 'EN_COCINA', texto: 'En cocina' },
    { valor: 'CON_EL_REPARTIDOR', texto: 'Con el repartidor' },
    { valor: 'ENTREGADO', texto: 'Entregados' }
  ];

  constructor(
    private pedidoService: PedidoService,
    private restauranteService: RestauranteService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.cargarPedidos();
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
        this.pedidos = pedidos.map(pedido => ({
          ...pedido,
          fechaFormateada: this.formatearFecha(pedido.fechaCreacion),
          estadoColor: this.obtenerColorEstado(pedido.estado),
          estadoTexto: this.obtenerTextoEstado(pedido.estado)
        }));
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
      this.pedidosFiltrados = this.pedidos.filter(p => p.estado === this.filtroEstado);
    }

    if (this.ordenamiento === 'reciente') {
      this.pedidosFiltrados.sort((a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime());
    } else if (this.ordenamiento === 'antiguo') {
      this.pedidosFiltrados.sort((a, b) => new Date(a.fechaCreacion).getTime() - new Date(b.fechaCreacion).getTime());
    } else if (this.ordenamiento === 'mayor') {
      this.pedidosFiltrados.sort((a, b) => b.total - a.total);
    } else if (this.ordenamiento === 'menor') {
      this.pedidosFiltrados.sort((a, b) => a.total - b.total);
    }
  }

  cambiarFiltroEstado(estado: string): void { this.filtroEstado = estado; this.aplicarFiltros(); }
  cambiarOrdenamiento(orden: string): void { this.ordenamiento = orden; this.aplicarFiltros(); }
  
  verDetallePedido(pedidoId: string): void {
    this.loadingDetalles = true;
    this.pedidoService.obtenerPedido(pedidoId).subscribe({
      next: (pedidoCompleto) => {
        if (pedidoCompleto.items && pedidoCompleto.items.length > 0) {
          this.cargarInformacionProductos(pedidoCompleto);
        } else {
          this.mostrarPedidoEnModal(pedidoCompleto);
        }
      },
      error: () => {
        const pedido = this.pedidos.find(p => p.id === pedidoId);
        if (pedido) this.mostrarPedidoEnModal(pedido);
      }
    });
  }

  private cargarInformacionProductos(pedido: Pedido): void {
    const observables = pedido.items!.map(item => 
      this.restauranteService.getProductosByRestaurante(pedido.restauranteId).pipe(
        map(productos => ({ item, producto: productos.find(p => p.id === item.productoId) })),
        catchError(() => of({ item, producto: null }))
      )
    );

    forkJoin(observables).subscribe({
      next: (resultados) => {
        const itemsConProductos = resultados.map(({ item, producto }) => ({
          ...item,
          nombreProducto: producto?.nombre || 'Producto no disponible',
          descripcionProducto: producto?.descripcion || '',
          imagenProducto: producto?.imagenUrl || ''
        }));
        this.mostrarPedidoEnModal({ ...pedido, items: itemsConProductos });
      }
    });
  }

  private mostrarPedidoEnModal(pedido: Pedido): void {
    this.pedidoSeleccionado = {
      ...pedido,
      fechaFormateada: this.formatearFecha(pedido.fechaCreacion),
      estadoColor: this.obtenerColorEstado(pedido.estado),
      estadoTexto: this.obtenerTextoEstado(pedido.estado)
    };
    this.mostrarModal = true;
    this.loadingDetalles = false;
    document.body.style.overflow = 'hidden';
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.pedidoSeleccionado = null;
    document.body.style.overflow = 'auto';
  }

  // ⭐ LÓGICA DE CALIFICACIÓN
  abrirModalCalificacion(pedido: PedidoDetallado, event: Event): void {
    event.stopPropagation();
    this.pedidoParaCalificar = pedido;
    this.calificacionRestaurante = 0;
    this.comentarioRestaurante = '';
    this.calificacionRepartidor = 0;
    this.comentarioRepartidor = '';
    this.mostrarModalCalificacion = true;
    document.body.style.overflow = 'hidden';
  }

  cerrarModalCalificacion(): void {
    this.mostrarModalCalificacion = false;
    this.pedidoParaCalificar = null;
    document.body.style.overflow = 'auto';
  }

  setCalificacionRestaurante(stars: number): void { this.calificacionRestaurante = stars; }
  setCalificacionRepartidor(stars: number): void { this.calificacionRepartidor = stars; }

  enviarCalificacionRestaurante(): void {
    if (!this.pedidoParaCalificar || this.calificacionRestaurante === 0) return;
    this.enviandoCalificacionRestaurante = true;
    
    this.pedidoService.calificarRestaurante(
      this.pedidoParaCalificar.id,
      this.calificacionRestaurante,
      this.comentarioRestaurante
    ).subscribe({
      next: () => {
        alert('¡Gracias por calificar al restaurante!');
        this.calificacionRestaurante = 0;
        this.comentarioRestaurante = '';
        this.enviandoCalificacionRestaurante = false;
        this.checkCerrarModal();
      },
      error: (err) => {
        console.error('Error calificar restaurante:', err);
        alert('Error al enviar calificación del restaurante.');
        this.enviandoCalificacionRestaurante = false;
      }
    });
  }

  enviarCalificacionRepartidor(): void {
    if (!this.pedidoParaCalificar || this.calificacionRepartidor === 0) return;
    this.enviandoCalificacionRepartidor = true;

    this.pedidoService.calificarRepartidor(
      this.pedidoParaCalificar.id,
      this.calificacionRepartidor,
      this.comentarioRepartidor
    ).subscribe({
      next: () => {
        alert('¡Gracias por calificar al repartidor!');
        this.calificacionRepartidor = 0;
        this.comentarioRepartidor = '';
        this.enviandoCalificacionRepartidor = false;
        this.checkCerrarModal();
      },
      error: (err) => {
        console.error('Error calificar repartidor:', err);
        alert('Error al enviar calificación del repartidor.');
        this.enviandoCalificacionRepartidor = false;
      }
    });
  }

  // Cerrar modal si ya se enviaron ambas o el usuario quiere salir
  checkCerrarModal(): void {
    // Opcional: lógica para cerrar automático si ambas calificaciones están en 0 (reseteadas tras envío)
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  }
  formatearId(id: string | undefined): string { return id ? id.substring(0, 8) : 'N/A'; }
  
  obtenerColorEstado(estado: string): string {
    const colores: any = { 'INICIADO': 'bg-yellow-100 text-yellow-800 border-yellow-300', 'EN_COCINA': 'bg-blue-100 text-blue-800 border-blue-300', 'CON_EL_REPARTIDOR': 'bg-purple-100 text-purple-800 border-purple-300', 'ENTREGADO': 'bg-green-100 text-green-800 border-green-300' };
    return colores[estado] || 'bg-gray-100 text-gray-800';
  }

  obtenerTextoEstado(estado: string): string {
    const textos: any = { 'INICIADO': 'Iniciado', 'EN_COCINA': 'En cocina', 'CON_EL_REPARTIDOR': 'Con repartidor', 'ENTREGADO': 'Entregado' };
    return textos[estado] || estado;
  }

  obtenerIconoEstado(estado: string): string {
    const iconos: any = { 'INICIADO': '⏳', 'EN_COCINA': '👨‍🍳', 'CON_EL_REPARTIDOR': '🚴', 'ENTREGADO': '✅' };
    return iconos[estado] || '📦';
  }

  formatearPrecio(precio: number): string {
    return isNaN(precio) ? 'N/A' : new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(precio);
  }

  obtenerCodigoEntrega(pedidoId: string): void {
    console.log(' Obtener código de entrega para pedido:', pedidoId);

    this.pedidoService.obtenerCodigoEntrega(pedidoId).subscribe({
      next: (codigo: string) => {
        console.log(' Código de entrega recibido:', codigo);
        alert('Tu código de entrega es: ' + codigo);
      },
      error: (error) => {
        console.error('Error al obtener código de entrega:', error);
        alert('No se pudo obtener el código de entrega. Intenta nuevamente.');
      }
    });
  }
}
