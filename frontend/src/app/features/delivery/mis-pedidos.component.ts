import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DeliveryService, PedidoNotificacion } from './delivery.service';
import { DeliveryHeaderComponent } from './header.component';

@Component({
  selector: 'app-delivery-mis-pedidos',
  standalone: true,
  imports: [CommonModule, RouterModule, DeliveryHeaderComponent],
  templateUrl: './mis-pedidos.component.html'
})
export class DeliveryMisPedidosComponent implements OnInit {
  pedidos: PedidoNotificacion[] = [];
  loading = false;
  errorMessage: string | null = null;

  private platformId = inject(PLATFORM_ID);

  constructor(private deliveryService: DeliveryService) {}

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.loading = true;
    this.errorMessage = null;

    const repartidorId = typeof localStorage !== 'undefined' 
      ? localStorage.getItem('quick-delivery-userId') 
      : null;

    if (!repartidorId) {
      this.errorMessage = 'No se pudo identificar al repartidor. Por favor, inicia sesión nuevamente.';
      this.loading = false;
      return;
    }

    this.deliveryService.obtenerPedidosAceptados(repartidorId).subscribe({
      next: (pedidos) => {
        console.log('✅ Pedidos aceptados recibidos:', pedidos);
        this.pedidos = pedidos.sort((a, b) => 
          new Date(b.fechaAceptacion || b.fechaCreacion).getTime() - 
          new Date(a.fechaAceptacion || a.fechaCreacion).getTime()
        );
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Error al cargar pedidos:', err);
        this.errorMessage = 'Error al cargar tus pedidos. Por favor, intenta de nuevo.';
        this.loading = false;
      }
    });
  }

  formatearId(id: string): string {
    if (!id) return 'N/A';
    return id.substring(0, 8);
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  obtenerColorEstado(estado: string): string {
    const colores: { [key: string]: string } = {
      'PENDIENTE': 'bg-yellow-100 text-yellow-800 border-yellow-300',
      'ACEPTADO': 'bg-green-100 text-green-800 border-green-300',
      'RECHAZADO': 'bg-red-100 text-red-800 border-red-300'
    };
    return colores[estado] || 'bg-gray-100 text-gray-800 border-gray-300';
  }

  obtenerTextoEstado(estado: string): string {
    const textos: { [key: string]: string } = {
      'PENDIENTE': 'Pendiente',
      'ACEPTADO': 'Aceptado',
      'RECHAZADO': 'Rechazado'
    };
    return textos[estado] || estado;
  }
}

