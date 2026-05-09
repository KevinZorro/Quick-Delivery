import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from '../edge/auth.service';
import { PedidosService, PedidoDto } from './pedidos.service';
import { ProductoResponse, ProductoService } from './producto.service';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedidos.component.html'
})
export class PedidosComponent implements OnInit {

  pedidos: PedidoDto[] = [];
  pedidosVisibles: PedidoDto[] = [];
  cargando = true;
  mensaje = '';
  cargandoCambio: string | null = null;
  restauranteNombre = '';
  filtroEstado = 'TODOS';
  ordenPedidos: 'RECIENTES' | 'ANTIGUOS' | 'ESTADO' = 'RECIENTES';

  private platformId = inject(PLATFORM_ID);

  estados = [
    'NUEVO',
    'ACEPTADO',
    'EN_COCINA',
    'CON_EL_REPARTIDOR',
    'ENTREGADO',
    'RECHAZADO_POR_RESTAURANTE'
  ];

  restauranteId: string = ''; // ⭐ No hardcodeado, se obtiene del usuario autenticado

  constructor(
    private pedidosService: PedidosService,
    private productoService: ProductoService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.restauranteNombre = localStorage.getItem('quick-delivery-userName') || '';
      
      // ⭐ Obtener restauranteId del usuario autenticado
      const restauranteIdFromStorage = localStorage.getItem('quick-delivery-restauranteId');
      if (restauranteIdFromStorage) {
        this.restauranteId = restauranteIdFromStorage;
      } else {
        this.mensaje = 'Error: No se pudo obtener el ID del restaurante. Por favor inicia sesión nuevamente.';
        this.cargando = false;
        return;
      }

      if (!this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }
    }

    this.cargarHistorial();
  }

  cargarHistorial(): void {
    this.cargando = true;

    this.pedidosService.getHistorial(this.restauranteId).subscribe({
      next: data => {
        this.enriquecerPedidosConProductos(data);
      },
      error: err => {
        console.error(err);
        this.cargando = false;
        this.mensaje = 'Error cargando pedidos';
      }
    });
  }

  cambiarEstado(pedidoId: string, nuevoEstado: string): void {
    const pedido = this.pedidos.find(item => item.id === pedidoId);
    if (!pedido || !this.esTransicionPermitida(pedido.estado, nuevoEstado)) {
      this.mensaje = 'Cambio de estado no permitido';
      setTimeout(() => this.mensaje = '', 2500);
      return;
    }

    this.cargandoCambio = pedidoId;

    this.pedidosService.actualizarEstado(pedidoId, nuevoEstado).subscribe({
      next: () => {
        this.mensaje = 'Estado actualizado';
        this.cargandoCambio = null;
        this.cargarHistorial();
        setTimeout(() => this.mensaje = '', 2000);
      },
      error: err => {
        console.error(err);
        this.mensaje = 'Error al actualizar';
        this.cargandoCambio = null;
      }
    });
  }

  aceptarPedido(pedidoId: string): void {
    this.cargandoCambio = pedidoId;
    
    this.pedidosService.aceptarPedido(pedidoId).subscribe({
      next: () => {
        this.mensaje = 'Pedido aceptado correctamente';
        this.cargandoCambio = null;
        this.cargarHistorial();
        setTimeout(() => this.mensaje = '', 2500);
      },
      error: err => {
        console.error(err);
        this.mensaje = 'Error al aceptar el pedido';
        this.cargandoCambio = null;
      }
    });
  }

  rechazarPedido(pedidoId: string): void {
    this.cargandoCambio = pedidoId;
    
    this.pedidosService.rechazarPedido(pedidoId).subscribe({
      next: () => {
        this.mensaje = 'Pedido rechazado';
        this.cargandoCambio = null;
        this.cargarHistorial();
        setTimeout(() => this.mensaje = '', 2500);
      },
      error: err => {
        console.error(err);
        this.mensaje = 'Error al rechazar el pedido';
        this.cargandoCambio = null;
      }
    });
  }

  irAProductos(): void {
    this.router.navigate(['/restaurante/main']);
  }

  irAPromociones(): void {
    this.router.navigate(['/restaurante/main'], { fragment: 'seccion-promociones' });
  }

  verReportes(): void {
    this.router.navigate(['/dashboard-reportes']);
  }

  verDirecciones(): void {
    this.router.navigate(['/restaurante/direcciones']);
  }

  verHorarios(): void {
    this.router.navigate(['/restaurante/horarios']);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private enriquecerPedidosConProductos(pedidos: PedidoDto[]): void {
    this.productoService.obtenerProductosPorRestaurante(this.restauranteId).pipe(
      map(productos => this.aplicarProductosAPedidos(pedidos, productos)),
      catchError(error => {
        console.error('Error cargando productos del restaurante:', error);
        return of(pedidos);
      })
    ).subscribe(pedidosEnriquecidos => {
      this.pedidos = [...pedidosEnriquecidos].sort((a, b) =>
        new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
      );
      this.aplicarOrdenYFiltro();
      this.cargando = false;
    });
  }

  private aplicarProductosAPedidos(pedidos: PedidoDto[], productos: ProductoResponse[]): PedidoDto[] {
    const productosPorId = new Map(productos.map(producto => [producto.id, producto]));

    return pedidos.map(pedido => ({
      ...pedido,
      items: pedido.items?.map(item => {
        const producto = productosPorId.get(item.productoId);

        return {
          ...item,
          nombreProducto: this.debeReemplazarNombreProducto(item.nombreProducto) ? producto?.nombre : item.nombreProducto,
          categoria: item.categoria || producto?.categoria,
          imagenUrl: item.imagenUrl || producto?.imagenUrl
        };
      }) || []
    }));
  }

  private debeReemplazarNombreProducto(nombre?: string): boolean {
    return !nombre || nombre === 'Producto no disponible' || nombre.startsWith('Producto ');
  }

  pedidosPorEstado(estado: string): number {
    return this.pedidos.filter(pedido => pedido.estado === estado).length;
  }

  cambiarFiltroEstado(estado: string): void {
    this.filtroEstado = estado;
    this.aplicarOrdenYFiltro();
  }

  cambiarOrden(event: Event): void {
    this.ordenPedidos = (event.target as HTMLSelectElement).value as 'RECIENTES' | 'ANTIGUOS' | 'ESTADO';
    this.aplicarOrdenYFiltro();
  }

  private aplicarOrdenYFiltro(): void {
    const prioridadEstado: Record<string, number> = {
      NUEVO: 1,
      ACEPTADO: 2,
      EN_COCINA: 3,
      CON_EL_REPARTIDOR: 4,
      ENTREGADO: 5,
      RECHAZADO_POR_RESTAURANTE: 6
    };

    const pedidosFiltrados = this.filtroEstado === 'TODOS'
      ? [...this.pedidos]
      : this.pedidos.filter(pedido => pedido.estado === this.filtroEstado);

    this.pedidosVisibles = pedidosFiltrados.sort((a, b) => {
      if (this.ordenPedidos === 'ANTIGUOS') {
        return new Date(a.fechaCreacion).getTime() - new Date(b.fechaCreacion).getTime();
      }

      if (this.ordenPedidos === 'ESTADO') {
        const estadoA = prioridadEstado[a.estado] || 99;
        const estadoB = prioridadEstado[b.estado] || 99;
        if (estadoA !== estadoB) {
          return estadoA - estadoB;
        }
      }

      return new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime();
    });
  }

  estadosDisponiblesParaPedido(pedido: PedidoDto): string[] {
    // Los pedidos NUEVO solo pueden ser aceptados o rechazados
    if (pedido.estado === 'NUEVO') {
      return ['ACEPTADO', 'RECHAZADO_POR_RESTAURANTE'];
    }

    // Las transiciones normales
    const siguienteEstado: Record<string, string> = {
      ACEPTADO: 'EN_COCINA',
      EN_COCINA: 'CON_EL_REPARTIDOR'
    };

    const siguiente = siguienteEstado[pedido.estado];
    return siguiente ? [siguiente] : [];
  }

  puedeCambiarEstado(pedido: PedidoDto): boolean {
    return this.estadosDisponiblesParaPedido(pedido).length > 0;
  }

  private esTransicionPermitida(estadoActual: string, nuevoEstado: string): boolean {
    // Transiciones permitidas
    const transiciones: Record<string, string[]> = {
      NUEVO: ['ACEPTADO', 'RECHAZADO_POR_RESTAURANTE'],
      ACEPTADO: ['EN_COCINA'],
      EN_COCINA: ['CON_EL_REPARTIDOR']
    };

    const estadosPermitidos = transiciones[estadoActual] || [];
    return estadosPermitidos.includes(nuevoEstado);
  }

  totalItems(pedido: PedidoDto): number {
    return pedido.items?.reduce((total, item) => total + Number(item.cantidad || 0), 0) || 0;
  }

  nombreEstado(estado: string): string {
    const labels: Record<string, string> = {
      NUEVO: 'Nuevo pedido',
      ACEPTADO: 'Aceptado',
      EN_COCINA: 'En cocina',
      CON_EL_REPARTIDOR: 'Con repartidor',
      ENTREGADO: 'Entregado',
      RECHAZADO_POR_RESTAURANTE: 'Rechazado'
    };
    return labels[estado] || estado;
  }

  estadoClasses(estado: string): string {
    const classes: Record<string, string> = {
      NUEVO: 'bg-blue-50 text-blue-800 border-blue-200',
      ACEPTADO: 'bg-amber-50 text-amber-800 border-amber-200',
      EN_COCINA: 'bg-orange-50 text-orange-800 border-orange-200',
      CON_EL_REPARTIDOR: 'bg-sky-50 text-sky-800 border-sky-200',
      ENTREGADO: 'bg-emerald-50 text-emerald-800 border-emerald-200',
      RECHAZADO_POR_RESTAURANTE: 'bg-red-50 text-red-800 border-red-200'
    };
    return classes[estado] || 'bg-gray-50 text-gray-700 border-gray-200';
  }

  productoNombre(item: { nombreProducto?: string; productoId: string }): string {
    return item.nombreProducto || `Producto ${item.productoId.slice(0, 8)}`;
  }

  trackByPedidoId(_: number, pedido: PedidoDto): string {
    return pedido.id;
  }

  trackByProductoId(index: number, item: { productoId: string }): string {
    return `${item.productoId}-${index}`;
  }
}
