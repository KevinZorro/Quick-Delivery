import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RestauranteService, Restaurante, Producto, ResenaRestaurante } from './restaurante.service';
import { CarritoService, CarritoItem } from './carrito.service';
import { PedidoService } from './pedido.service';
import { DireccionService, Direccion } from './direccion.service';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-restaurante-detalle',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './restaurante-detalle.component.html'
})
export class RestauranteDetalleComponent implements OnInit, OnDestroy {
  restaurante: Restaurante | null = null;
  productos: Producto[] = [];
  resenas: ResenaRestaurante[] = []; // ⭐ Lista de reseñas
  loading = true;
  errorMessage: string | null = null;
  restauranteId: string = '';

  cantidades: { [productoId: string]: number } = {};
  preferencias: { [productoId: string]: string } = {};

  // Panel de carrito
  panelCarritoAbierto = false;
  carritoItems: CarritoItem[] = [];
  totalCarrito: number = 0;

  // Modal confirmar cerrar
  modalConfirmarCerrar = false;

  // Modal de pago
  modalPagoAbierto = false;
  metodoPagoSeleccionado: string = '';
  preferenciasPago: string = '';
  direccionSeleccionada: string = '';
  
  // Direcciones
  direcciones: Direccion[] = [];
  cargandoDirecciones = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restauranteService: RestauranteService,
    private carritoService: CarritoService,
    private pedidoService: PedidoService,
    private direccionService: DireccionService
  ) {}

  ngOnInit(): void {
    this.restauranteId = this.route.snapshot.paramMap.get('id') || '';
    
    if (this.restauranteId) {
      this.loadRestauranteData();
    }
    window.addEventListener('beforeunload', this.onBeforeUnload.bind(this));
    this.cargarCarrito();
    this.cargarDirecciones();
  }

  ngOnDestroy(): void {
    window.removeEventListener('beforeunload', this.onBeforeUnload.bind(this));
  }

  onBeforeUnload(event: any): string | undefined {
    if (this.carritoItems.length > 0) {
      event.preventDefault();
      event.returnValue = '¿Estás seguro de dejar la página? Se perderá el carrito.';
      return '¿Estás seguro de dejar la página? Se perderá el carrito.';
    }
    return undefined;
  }

  loadRestauranteData(): void {
    // 1. Cargar info restaurante
    this.restauranteService.getRestauranteById(this.restauranteId).subscribe({
      next: (data) => {
        this.restaurante = data;
      },
      error: () => {
        this.errorMessage = 'Error al cargar información del restaurante';
      }
    });

    // 2. Cargar productos
    this.restauranteService.getProductosByRestaurante(this.restauranteId).subscribe({
      next: (data) => {
        this.productos = data;
        this.loading = false;
        this.productos.forEach(p => this.cantidades[p.id] = 1);
      },
      error: () => {
        this.errorMessage = 'Error al cargar productos';
        this.loading = false;
      }
    });

    // ⭐ 3. Cargar reseñas
    this.restauranteService.getResenasRestaurante(this.restauranteId).subscribe({
      next: (data) => {
        this.resenas = data;
        console.log('💬 Reseñas cargadas:', this.resenas.length);
      },
      error: (err) => {
        console.error('❌ Error al cargar reseñas:', err);
      }
    });
  }

  cargarDirecciones(): void {
    this.cargandoDirecciones = true;
    this.direccionService.obtenerMisDirecciones().subscribe({
      next: (direcciones) => {
        this.direcciones = direcciones;
        this.cargandoDirecciones = false;
      },
      error: (err) => {
        console.error('❌ Error al cargar direcciones:', err);
        this.cargandoDirecciones = false;
      }
    });
  }

  volver(): void {
    this.router.navigate(['/main']);
  }

  agregarAlCarrito(producto: Producto): void {
    this.carritoService.agregarProducto(producto, 1);
    this.abrirPanelCarrito();
  }

  // ⭐ Helper para estrellas en HTML
  getEstrellas(calificacion: number): number[] {
    return Array(5).fill(0).map((_, i) => i + 1 <= calificacion ? 1 : 0);
  }

  // ========== CARRITO ==========
  abrirPanelCarrito(): void {
    this.cargarCarrito();
    this.panelCarritoAbierto = true;
  }

  cerrarPanelCarrito(): void {
    this.panelCarritoAbierto = false;
  }

  intentarCerrarPanel(): void {
    this.modalConfirmarCerrar = true;
  }

  confirmarCerrar(descartar: boolean): void {
    this.modalConfirmarCerrar = false;
    if (descartar) {
      this.vaciarCarrito();
      this.panelCarritoAbierto = false;
    }
  }

  cargarCarrito(): void {
    this.carritoItems = this.carritoService.obtenerCarrito();
    this.totalCarrito = this.carritoService.obtenerTotal();
  }

  cambiarCantidad(productoId: string, cambio: number): void {
    if (cambio === 1) {
      this.carritoService.incrementarCantidad(productoId);
    } else {
      this.carritoService.disminuirCantidad(productoId);
    }
    this.cargarCarrito();
  }

  eliminarItem(productoId: string): void {
    this.carritoService.disminuirCantidad(productoId);
    this.cargarCarrito();
  }

  vaciarCarrito(): void {
    this.carritoService.guardarCarrito([]);
    this.cargarCarrito();
  }

  // ========== PAGO ==========
  abrirModalPago(): void {
    if (this.carritoItems.length === 0) {
      alert('El carrito está vacío');
      return;
    }
    if (this.direcciones.length === 0) {
      alert('Debes agregar al menos una dirección de entrega antes de hacer un pedido');
      return;
    }
    this.modalPagoAbierto = true;
    this.metodoPagoSeleccionado = '';
    this.preferenciasPago = '';
    this.direccionSeleccionada = '';
  }

  cerrarModalPago(): void {
    this.modalPagoAbierto = false;
  }

  confirmarPago(): void {
    if (!this.direccionSeleccionada) {
      alert('Por favor selecciona una dirección de entrega');
      return;
    }
    if (!this.metodoPagoSeleccionado) {
      alert('Por favor selecciona un método de pago');
      return;
    }
    if (this.carritoItems.length === 0) {
      alert('El carrito está vacío');
      return;
    }

    const clienteId = localStorage.getItem('quick-delivery-userId');
    if (!clienteId) {
      alert('Error: No se pudo identificar el cliente. Por favor inicia sesión nuevamente.');
      this.router.navigate(['/login']);
      return;
    }

    const pedidoRequest = {
      clienteId: clienteId,
      restauranteId: this.restauranteId,
      metodoPago: this.metodoPagoSeleccionado,
      direccionEntregaId: this.direccionSeleccionada,
      preferencias: this.preferenciasPago,
      items: this.carritoItems.map(item => ({
        productoId: item.producto.id,
        cantidad: item.cantidad
      }))
    };

    this.pedidoService.crearPedidoDesdeCarrito(pedidoRequest).subscribe({
      next: (pedidoCreado) => {
        alert(`¡Pago realizado con éxito! Tu pedido ${pedidoCreado.id}`);
        this.vaciarCarrito();
        this.modalPagoAbierto = false;
        this.panelCarritoAbierto = false;
      },
      error: (error) => {
        let mensajeError = 'Error al procesar el pago';
        if (error.status === 401 || error.status === 403) {
          mensajeError = 'Tu sesión ha expirado. Por favor inicia sesión nuevamente.';
          this.router.navigate(['/login']);
        } else if (error.status === 0) {
          mensajeError = 'No se pudo conectar con el servidor.';
        } else if (error.error && typeof error.error === 'string') {
          mensajeError = error.error;
        }
        alert(mensajeError);
      }
    });
  }
}
