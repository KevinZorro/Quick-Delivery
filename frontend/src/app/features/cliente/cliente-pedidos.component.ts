import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService, Pedido, ItemPedido } from './pedido.service';
import { RestauranteService, Producto } from './restaurante.service';
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

  mostrarModal: boolean = false;
  pedidoSeleccionado: PedidoDetallado | null = null;
  loadingDetalles: boolean = false;

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
      this.error = 'No se pudo identificar al usuario. Por favor, inicia sesión nuevamente.';
      this.loading = false;
      return;
    }

    console.log('📦 Cargando pedidos del usuario:', usuarioId);

    this.pedidoService.listarPedidosUsuario(usuarioId).subscribe({
      next: (pedidos) => {
        console.log('✅ Pedidos recibidos:', pedidos);
        
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
        console.error('❌ Error al cargar pedidos:', error);
        this.error = 'No se pudieron cargar los pedidos. Por favor, intenta de nuevo.';
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

    if (this.ordenamiento === 'reciente') {
      this.pedidosFiltrados.sort((a, b) => 
        new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
      );
    } else if (this.ordenamiento === 'antiguo') {
      this.pedidosFiltrados.sort((a, b) => 
        new Date(a.fechaCreacion).getTime() - new Date(b.fechaCreacion).getTime()
      );
    } else if (this.ordenamiento === 'mayor') {
      this.pedidosFiltrados.sort((a, b) => b.total - a.total);
    } else if (this.ordenamiento === 'menor') {
      this.pedidosFiltrados.sort((a, b) => a.total - b.total);
    }
  }

  cambiarFiltroEstado(estado: string): void {
    this.filtroEstado = estado;
    this.aplicarFiltros();
  }

  cambiarOrdenamiento(orden: string): void {
    this.ordenamiento = orden;
    this.aplicarFiltros();
  }

  // ✅ NUEVO: Cargar detalles del pedido con información de productos
  verDetallePedido(pedidoId: string): void {
    console.log('🔍 Cargando detalles completos del pedido:', pedidoId);
    this.loadingDetalles = true;
    
    this.pedidoService.obtenerPedido(pedidoId).subscribe({
      next: (pedidoCompleto) => {
        console.log('✅ Pedido completo recibido:', pedidoCompleto);
        
        // Si el pedido tiene items, cargar información de productos
        if (pedidoCompleto.items && pedidoCompleto.items.length > 0) {
          this.cargarInformacionProductos(pedidoCompleto);
        } else {
          // Si no hay items, mostrar el pedido sin productos
          this.mostrarPedidoEnModal(pedidoCompleto);
        }
      },
      error: (error) => {
        console.error('❌ Error al cargar detalles del pedido:', error);
        this.loadingDetalles = false;
        
        // Fallback: usar el pedido de la lista actual
        const pedido = this.pedidos.find(p => p.id === pedidoId);
        if (pedido) {
          this.mostrarPedidoEnModal(pedido);
        }
      }
    });
  }

  // ✅ NUEVO: Cargar información de productos desde el microservicio
  private cargarInformacionProductos(pedido: Pedido): void {
    console.log('🛒 Cargando información de productos...');

    // Crear array de observables para cada producto
    const productosObservables = pedido.items!.map(item => 
      this.restauranteService.getProductosByRestaurante(pedido.restauranteId).pipe(
        map(productos => {
          // Buscar el producto específico por ID
          const producto = productos.find(p => p.id === item.productoId);
          return { item, producto };
        }),
        catchError(error => {
          console.error(`❌ Error al cargar producto ${item.productoId}:`, error);
          return of({ item, producto: null });
        })
      )
    );

    // Esperar a que todas las peticiones terminen
    forkJoin(productosObservables).subscribe({
      next: (resultados) => {
        console.log('✅ Información de productos cargada:', resultados);

        // Actualizar items con información de productos
        const itemsConProductos = resultados.map(({ item, producto }) => ({
          ...item,
          nombreProducto: producto?.nombre || 'Producto no disponible',
          descripcionProducto: producto?.descripcion || '',
          imagenProducto: producto?.imagenUrl || ''
        }));

        // Crear pedido actualizado con items completos
        const pedidoActualizado = {
          ...pedido,
          items: itemsConProductos
        };

        this.mostrarPedidoEnModal(pedidoActualizado);
      },
      error: (error) => {
        console.error('❌ Error al cargar información de productos:', error);
        this.mostrarPedidoEnModal(pedido);
      }
    });
  }

  // ✅ NUEVO: Mostrar pedido en el modal
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
    
    console.log('✅ Modal abierto con pedido:', this.pedidoSeleccionado);
  }

  cerrarModal(): void {
    console.log('❌ Cerrando modal');
    this.mostrarModal = false;
    this.pedidoSeleccionado = null;
    this.loadingDetalles = false;
    document.body.style.overflow = 'auto';
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const opciones: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    };
    return date.toLocaleDateString('es-ES', opciones);
  }

  formatearId(id: string | undefined | null): string {
    if (!id) return 'N/A';
    return id.substring(0, 8);
  }

  obtenerColorEstado(estado: string): string {
    const colores: { [key: string]: string } = {
      'INICIADO': 'bg-yellow-100 text-yellow-800 border-yellow-300',
      'EN_COCINA': 'bg-blue-100 text-blue-800 border-blue-300',
      'CON_EL_REPARTIDOR': 'bg-purple-100 text-purple-800 border-purple-300',
      'ENTREGADO': 'bg-green-100 text-green-800 border-green-300'
    };
    return colores[estado] || 'bg-gray-100 text-gray-800 border-gray-300';
  }

  obtenerTextoEstado(estado: string): string {
    const textos: { [key: string]: string } = {
      'INICIADO': 'Iniciado',
      'EN_COCINA': 'En cocina',
      'CON_EL_REPARTIDOR': 'Con el repartidor',
      'ENTREGADO': 'Entregado'
    };
    return textos[estado] || estado;
  }

  obtenerIconoEstado(estado: string): string {
    const iconos: { [key: string]: string } = {
      'INICIADO': '⏳',
      'EN_COCINA': '👨‍🍳',
      'CON_EL_REPARTIDOR': '🚴',
      'ENTREGADO': '✅'
    };
    return iconos[estado] || '📦';
  }

  formatearPrecio(precio: number): string {
    // ✅ Verificar si es NaN
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
    
    return this.pedidoSeleccionado.items.reduce((total, item) => total + item.cantidad, 0);
  }
}
