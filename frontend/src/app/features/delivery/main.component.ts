import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DeliveryService } from './delivery.service';

@Component({
  selector: 'app-delivery-main',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './main.component.html'
})
export class DeliveryMainComponent implements OnInit {

  readonly repartidorId = 'ecc145a3-244b-43f9-9f18-37680c97009a';

  // UI
  domiciliarioNombre = '';
  disponible = true;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Filtros
  filtroActual: string = 'TODOS';

  // Pedidos
  pedidos: any[] = [];
  pedidosFiltrados: any[] = [];

  constructor(
    private router: Router,
    private deliveryService: DeliveryService
  ) {}

  ngOnInit(): void {
    this.domiciliarioNombre =
      localStorage.getItem('quick-delivery-userName') || 'Domiciliario';

    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.loading = true;

    this.deliveryService.getHistorial(this.repartidorId, this.filtroActual)
      .subscribe({
        next: (data) => {
          this.pedidos = data;
          this.aplicarFiltro();
          this.loading = false;
        },
        error: (e) => {
          console.error(e);
          this.errorMessage = 'Error cargando pedidos.';
          this.loading = false;
        }
      });
  }

  aplicarFiltro(): void {
    if (this.filtroActual === 'TODOS') {
      this.pedidosFiltrados = this.pedidos;
    } else {
      this.pedidosFiltrados = this.pedidos.filter(
        p => p.estado === this.filtroActual
      );
    }
  }

  filtrarPedidos(estado: string): void {
    this.filtroActual = estado;
    this.cargarPedidos();
  }

  getEstadoClass(estado: string): string {
    switch (estado) {
      case 'ASIGNADO': return 'bg-orange-100 text-orange-800';
      case 'EN_CAMINO': return 'bg-blue-100 text-blue-800';
      case 'ENTREGADO': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-200 text-gray-800';
    }
  }

  getEstadoTexto(estado: string): string {
    return estado.replace('_', ' ');
  }

  cerrarSesion(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
