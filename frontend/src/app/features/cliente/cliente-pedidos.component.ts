import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidoService, Pedido } from './pedido.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from './header.component';

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

  // ✅ Variables para el modal
  mostrarModal: boolean = false;
  pedidoSeleccionado: PedidoDetallado | null = null;

  estados = [
    { valor: 'TODOS', texto: 'Todos los pedidos' },
    { valor: 'INICIADO', texto: 'Iniciados' },
    { valor: 'EN_COCINA', texto: 'En cocina' },
    { valor: 'CON_EL_REPARTIDOR', texto: 'Con el repartidor' },
    { valor: 'ENTREGADO', texto: 'Entregados' }
  ];

  constructor(
    private pedidoService: PedidoService,
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

  // ✅ NUEVO: Abrir modal con detalles del pedido
  verDetallePedido(pedidoId: string): void {
    console.log('🔍 Abriendo detalles del pedido:', pedidoId);
    
    // Buscar el pedido en la lista actual
    const pedido = this.pedidos.find(p => p.id === pedidoId);
    
    if (pedido) {
      this.pedidoSeleccionado = pedido;
      this.mostrarModal = true;
      
      // Prevenir scroll del body cuando el modal está abierto
      document.body.style.overflow = 'hidden';
    } else {
      console.error('❌ Pedido no encontrado');
    }
  }

  // ✅ NUEVO: Cerrar modal
  cerrarModal(): void {
    console.log('❌ Cerrando modal');
    this.mostrarModal = false;
    this.pedidoSeleccionado = null;
    
    // Restaurar scroll del body
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
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(precio);
  }
}
