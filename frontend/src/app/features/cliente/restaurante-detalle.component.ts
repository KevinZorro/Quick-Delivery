import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { RestauranteService, Restaurante, Producto } from './restaurante.service';
import { CarritoService } from './carrito.service';
import { FormsModule } from '@angular/forms';
import { CarritoItem } from './carrito.service';
import { HttpClient } from '@angular/common/http';

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
  estadoPedido = 'INICIADO';

  // Cliente (deberías obtenerlo del servicio de autenticación)
  clienteId: string = ''; // ⚠️ Debes obtener esto del usuario autenticado

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router,
    private restauranteService: RestauranteService,
    private carritoService: CarritoService
  ) {}

  ngOnInit(): void {
    this.restauranteId = this.route.snapshot.paramMap.get('id') || '';
    
    // ⚠️ IMPORTANTE: Obtén el clienteId del usuario autenticado
    // Ejemplo: this.clienteId = this.authService.getCurrentUserId();
    // Por ahora lo hardcodeo, pero debes reemplazarlo:
    this.clienteId = 'uuid-del-cliente-actual'; // 🔴 CAMBIAR POR EL REAL
    
    if (this.restauranteId) {
      this.loadRestauranteData();
    }
    window.addEventListener('beforeunload', this.onBeforeUnload.bind(this));
    this.cargarCarrito();
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
    this.modalPagoAbierto = true;
    this.metodoPagoSeleccionado = '';
    this.preferenciasPago = '';
  }

  cerrarModalPago(): void {
    this.modalPagoAbierto = false;
  }

  confirmarPago(): void {
    if (!this.metodoPagoSeleccionado) {
      alert('Por favor selecciona un método de pago');
      return;
    }

    if (!this.clienteId) {
      alert('Error: No se pudo identificar el cliente');
      return;
    }

    // Construir el request con los items del carrito
    const pedidoRequest = {
      clienteId: this.clienteId,
      restauranteId: this.restauranteId,
      direccionEntregaId: null, // Si tienes dirección, agrégala aquí
      preferencias: this.preferenciasPago,
      items: this.carritoItems.map(item => ({
        productoId: item.producto.id,
        cantidad: item.cantidad
      }))
    };

    console.log('Enviando pedido:', pedidoRequest);

    // ⭐ Crear el pedido usando el nuevo endpoint
    this.http.post('/api/pedidos/crear-desde-carrito', pedidoRequest).subscribe({
      next: (pedidoCreado: any) => {
        console.log('Pedido creado:', pedidoCreado);

        // Actualizar estado a EN_COCINA
        this.http.patch(`/api/pedidos/${pedidoCreado.id}/estado`, null, { 
          params: { estado: 'EN_COCINA' }
        }).subscribe({
          next: () => console.log('Estado actualizado a EN_COCINA'),
          error: (err) => console.error('Error al actualizar estado:', err)
        });
        
        // Actualizar método de pago
        this.http.patch(`/api/pedidos/${pedidoCreado.id}/metodopago`, null, { 
          params: { metodoPago: this.metodoPagoSeleccionado }
        }).subscribe({
          next: () => console.log('Método de pago actualizado'),
          error: (err) => console.error('Error al actualizar método de pago:', err)
        });

        // Limpiar y cerrar todo
        alert('¡Pago realizado con éxito! El pedido está EN COCINA');
        this.vaciarCarrito();
        this.modalPagoAbierto = false;
        this.panelCarritoAbierto = false;
        this.estadoPedido = 'EN_COCINA';

        // Opcional: redirigir a página de seguimiento del pedido
        // this.router.navigate(['/pedidos', pedidoCreado.id]);
      },
      error: (error) => {
        console.error('Error completo:', error);
        alert('Error al procesar el pago: ' + (error.error?.message || error.message));
      }
    });
  }
}
