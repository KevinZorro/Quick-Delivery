import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RestauranteService, Restaurante, Producto } from './restaurante.service';
import { CarritoService } from './carrito.service';
import { PedidoService } from './pedido.service';
import { DireccionService, Direccion } from './direccion.service'; // ⭐ IMPORTAR
import { FormsModule } from '@angular/forms';
import { CarritoItem } from './carrito.service';

@Component({
  selector: 'app-restaurante-detalle',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './restaurante-detalle.component.html'
})
export class RestauranteDetalleComponent implements OnInit, OnDestroy {
  restaurante: Restaurante | null = null;
  productos: Producto[] = [];
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
  direccionSeleccionada: string = ''; // ⭐ NUEVO
  estadoPedido = 'INICIADO';

  // ⭐ NUEVO: Lista de direcciones
  direcciones: Direccion[] = [];
  cargandoDirecciones = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restauranteService: RestauranteService,
    private carritoService: CarritoService,
    private pedidoService: PedidoService,
    private direccionService: DireccionService // ⭐ INYECTAR
  ) {}

  ngOnInit(): void {
    this.restauranteId = this.route.snapshot.paramMap.get('id') || '';
    
    if (this.restauranteId) {
      this.loadRestauranteData();
    }
    window.addEventListener('beforeunload', this.onBeforeUnload.bind(this));
    this.cargarCarrito();
    this.cargarDirecciones(); // ⭐ CARGAR DIRECCIONES AL INICIAR
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
    this.restauranteService.getRestauranteById(this.restauranteId).subscribe({
      next: (data) => {
        this.restaurante = data;
      },
      error: () => {
        this.errorMessage = 'Error al cargar información del restaurante';
      }
    });

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
  }

  // ⭐ NUEVO: Cargar direcciones del cliente
  cargarDirecciones(): void {
    this.cargandoDirecciones = true;
    
    this.direccionService.obtenerMisDirecciones().subscribe({
      next: (direcciones) => {
        this.direcciones = direcciones;
        this.cargandoDirecciones = false;
        
        console.log('📍 Direcciones cargadas:', direcciones.length);
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

    // ⭐ Verificar que tenga direcciones
    if (this.direcciones.length === 0) {
      alert('Debes agregar al menos una dirección de entrega antes de hacer un pedido');
      return;
    }

    this.modalPagoAbierto = true;
    this.metodoPagoSeleccionado = '';
    this.preferenciasPago = '';
    this.direccionSeleccionada = ''; // ⭐ Resetear dirección
  }

  cerrarModalPago(): void {
    this.modalPagoAbierto = false;
  }

  confirmarPago(): void {
    // ⭐ Validar dirección seleccionada
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
      direccionEntregaId: this.direccionSeleccionada, // ⭐ INCLUIR DIRECCIÓN
      preferencias: this.preferenciasPago,
      items: this.carritoItems.map(item => ({
        productoId: item.producto.id,
        cantidad: item.cantidad
      }))
    };

    console.log('═══════════════════════════════════════');
    console.log('📦 ENVIANDO PEDIDO COMPLETO AL BACKEND');
    console.log('═══════════════════════════════════════');
    console.log('🆔 Cliente ID:', pedidoRequest.clienteId);
    console.log('🍽️ Restaurante ID:', pedidoRequest.restauranteId);
    console.log('💳 Método de pago:', pedidoRequest.metodoPago);
    console.log('📍 Dirección ID:', pedidoRequest.direccionEntregaId); // ⭐ LOG
    console.log('📝 Items:', pedidoRequest.items);
    console.log('💬 Preferencias:', pedidoRequest.preferencias);
    console.log('═══════════════════════════════════════');

    this.pedidoService.crearPedidoDesdeCarrito(pedidoRequest).subscribe({
      next: (pedidoCreado) => {
        console.log('✅ PEDIDO CREADO EXITOSAMENTE:', pedidoCreado);
        
        alert('¡Pago realizado con éxito! Tu pedido está siendo preparado.');
        this.vaciarCarrito();
        this.modalPagoAbierto = false;
        this.panelCarritoAbierto = false;
      },
      error: (error) => {
        console.error('❌ ERROR AL CREAR PEDIDO:', error);
        
        let mensajeError = 'Error al procesar el pago';
        
        if (error.status === 401 || error.status === 403) {
          mensajeError = 'Tu sesión ha expirado. Por favor inicia sesión nuevamente.';
          this.router.navigate(['/login']);
        } else if (error.status === 0) {
          mensajeError = 'No se pudo conectar con el servidor. Verifica tu conexión.';
        } else if (error.error && typeof error.error === 'string') {
          mensajeError = error.error;
        }
        
        alert(mensajeError);
      }
    });
  }
}
