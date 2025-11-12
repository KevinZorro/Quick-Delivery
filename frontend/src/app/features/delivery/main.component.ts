import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-delivery-main',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './main.component.html'
})
export class DeliveryMainComponent implements OnInit {
  domiciliarioNombre: string = '';
  disponible: boolean = true;
  loading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Estadísticas
  pedidosPendientes: number = 0;
  pedidosEnCamino: number = 0;
  pedidosEntregadosHoy: number = 0;
  gananciasHoy: number = 0;

  // Filtros
  filtroActual: string = 'TODOS';

  // Pedidos
  pedidos: any[] = [];
  pedidosFiltrados: any[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Obtener información del domiciliario
    this.domiciliarioNombre = localStorage.getItem('quick-delivery-userName') || 'Domiciliario';
    
    // Cargar pedidos
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.loading = true;
    
    // TODO: Aquí deberías llamar a tu servicio para obtener los pedidos
    // Ejemplo de datos mock:
    setTimeout(() => {
      this.pedidos = [
        {
          id: 1001,
          estado: 'ASIGNADO',
          restauranteNombre: 'Restaurante El Sabor',
          clienteNombre: 'Juan Pérez',
          direccionEntrega: 'Calle 10 #15-20, Barrio Centro',
          instruccionesEntrega: 'Tocar timbre 2 veces',
          total: 35000,
          fechaHora: new Date(),
          productos: [
            { nombre: 'Hamburguesa Clásica', cantidad: 2, subtotal: 25000 },
            { nombre: 'Papas Fritas', cantidad: 1, subtotal: 10000 }
          ]
        },
        {
          id: 1002,
          estado: 'EN_CAMINO',
          restauranteNombre: 'Pizza Express',
          clienteNombre: 'María García',
          direccionEntrega: 'Carrera 5 #8-30, Apto 301',
          instruccionesEntrega: null,
          total: 42000,
          fechaHora: new Date(),
          productos: [
            { nombre: 'Pizza Pepperoni Grande', cantidad: 1, subtotal: 42000 }
          ]
        }
      ];
      
      this.actualizarEstadisticas();
      this.filtrarPedidos(this.filtroActual);
      this.loading = false;
    }, 1000);
  }

  actualizarEstadisticas(): void {
    this.pedidosPendientes = this.pedidos.filter(p => p.estado === 'ASIGNADO').length;
    this.pedidosEnCamino = this.pedidos.filter(p => p.estado === 'EN_CAMINO').length;
    this.pedidosEntregadosHoy = this.pedidos.filter(p => p.estado === 'ENTREGADO').length;
    this.gananciasHoy = this.pedidos
      .filter(p => p.estado === 'ENTREGADO')
      .reduce((sum, p) => sum + (p.propina || 0), 0);
  }

  filtrarPedidos(estado: string): void {
    this.filtroActual = estado;
    if (estado === 'TODOS') {
      this.pedidosFiltrados = this.pedidos;
    } else {
      this.pedidosFiltrados = this.pedidos.filter(p => p.estado === estado);
    }
  }

  toggleDisponibilidad(): void {
    this.disponible = !this.disponible;
    this.successMessage = this.disponible 
      ? 'Ahora estás disponible para recibir pedidos' 
      : 'Ya no recibirás nuevos pedidos';
    setTimeout(() => this.successMessage = null, 3000);
  }

  iniciarEntrega(pedido: any): void {
    pedido.estado = 'EN_CAMINO';
    this.actualizarEstadisticas();
    this.filtrarPedidos(this.filtroActual);
    this.successMessage = `Entrega del pedido #${pedido.id} iniciada`;
    setTimeout(() => this.successMessage = null, 3000);
  }

  confirmarEntrega(pedido: any): void {
    pedido.estado = 'ENTREGADO';
    this.actualizarEstadisticas();
    this.filtrarPedidos(this.filtroActual);
    this.successMessage = `Pedido #${pedido.id} entregado correctamente`;
    setTimeout(() => this.successMessage = null, 3000);
  }

  verDetallePedido(pedido: any): void {
    console.log('Ver detalle del pedido:', pedido);
    // TODO: Implementar modal o navegación a página de detalle
  }

  abrirMapa(pedido: any): void {
    console.log('Abrir mapa para pedido:', pedido);
    // TODO: Implementar navegación a Google Maps o mapa integrado
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'ASIGNADO':
        return 'bg-orange-100 text-orange-800';
      case 'EN_CAMINO':
        return 'bg-blue-100 text-blue-800';
      case 'ENTREGADO':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getEstadoTexto(estado: string): string {
    switch (estado) {
      case 'ASIGNADO':
        return 'Asignado';
      case 'EN_CAMINO':
        return 'En Camino';
      case 'ENTREGADO':
        return 'Entregado';
      default:
        return estado;
    }
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('quick-delivery-userId');
    localStorage.removeItem('quick-delivery-userName');
    localStorage.removeItem('quick-delivery-userRole');
    this.router.navigate(['/login']);
  }
}
