import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DeliveryService, PedidoNotificacionConDistancia } from './delivery.service';
import { AuthService } from '../edge/auth.service';
import { DeliveryHeaderComponent } from './header.component';

@Component({
  selector: 'app-delivery-main',
  standalone: true,
  imports: [CommonModule, FormsModule, DeliveryHeaderComponent],
  templateUrl: './main.component.html'
})
export class DeliveryMainComponent implements OnInit {
  pedidos: PedidoNotificacionConDistancia[] = [];
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  
  // Ubicación GPS
  latitud: number = 0;
  longitud: number = 0;
  rangoMaximoKm: number = 10.0;
  ubicacionObtenida = false;
  obteniendoUbicacion = false;

  // Filtros
  ordenarPor: 'distancia' | 'fecha' = 'distancia';

  private platformId = inject(PLATFORM_ID);

  constructor(
    private deliveryService: DeliveryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Intentar obtener ubicación guardada
    if (typeof navigator !== 'undefined' && 'geolocation' in navigator) {
      this.obtenerUbicacionActual();
    } else {
      this.errorMessage = 'Tu navegador no soporta geolocalización. Por favor, ingresa tu ubicación manualmente.';
    }
  }

  obtenerUbicacionActual(): void {
    this.obteniendoUbicacion = true;
    this.errorMessage = null;

    if (!navigator.geolocation) {
      this.errorMessage = 'Geolocalización no está disponible en tu navegador';
      this.obteniendoUbicacion = false;
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        this.latitud = position.coords.latitude;
        this.longitud = position.coords.longitude;
        this.ubicacionObtenida = true;
        this.obteniendoUbicacion = false;
        
        // Guardar ubicación en localStorage
        if (typeof localStorage !== 'undefined') {
          localStorage.setItem('delivery-latitud', this.latitud.toString());
          localStorage.setItem('delivery-longitud', this.longitud.toString());
        }
        
        this.cargarPedidos();
      },
      (error) => {
        console.error('Error obteniendo ubicación:', error);
        this.errorMessage = 'No se pudo obtener tu ubicación. Por favor, verifica los permisos de geolocalización.';
        this.obteniendoUbicacion = false;
        
        // Intentar cargar ubicación guardada
        this.cargarUbicacionGuardada();
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0
      }
    );
  }

  cargarUbicacionGuardada(): void {
    if (typeof localStorage !== 'undefined') {
      const latGuardada = localStorage.getItem('delivery-latitud');
      const lngGuardada = localStorage.getItem('delivery-longitud');
      
      if (latGuardada && lngGuardada) {
        this.latitud = parseFloat(latGuardada);
        this.longitud = parseFloat(lngGuardada);
        this.ubicacionObtenida = true;
        this.cargarPedidos();
      }
    }
  }

  actualizarUbicacionManual(): void {
    if (this.latitud && this.longitud) {
      this.ubicacionObtenida = true;
      
      // Guardar en localStorage
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem('delivery-latitud', this.latitud.toString());
        localStorage.setItem('delivery-longitud', this.longitud.toString());
      }
      
      this.cargarPedidos();
    } else {
      this.errorMessage = 'Por favor, ingresa una ubicación válida';
    }
  }

  cargarPedidos(): void {
    if (!this.ubicacionObtenida || !this.latitud || !this.longitud) {
      this.errorMessage = 'Por favor, obtén tu ubicación primero';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.deliveryService.obtenerPedidosPendientesPorUbicacion(
      this.latitud,
      this.longitud,
      this.rangoMaximoKm
    ).subscribe({
      next: (pedidos) => {
        console.log('✅ Pedidos recibidos:', pedidos);
        this.pedidos = pedidos;
        this.ordenarPedidos();
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Error al cargar pedidos:', err);
        this.errorMessage = 'Error al cargar los pedidos disponibles. Por favor, intenta de nuevo.';
        this.loading = false;
      }
    });
  }

  ordenarPedidos(): void {
    if (this.ordenarPor === 'distancia') {
      this.pedidos.sort((a, b) => a.distanciaKm - b.distanciaKm);
    } else {
      this.pedidos.sort((a, b) => 
        new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
      );
    }
  }

  cambiarOrdenamiento(): void {
    this.ordenarPedidos();
  }

  aceptarPedido(notificacion: PedidoNotificacionConDistancia): void {
    if (!confirm(`¿Aceptar este pedido? Está a ${notificacion.distanciaFormateada} de distancia.`)) {
      return;
    }

    const repartidorId = localStorage.getItem('quick-delivery-userId');
    if (!repartidorId) {
      this.errorMessage = 'No se pudo identificar al repartidor. Por favor, inicia sesión nuevamente.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    this.deliveryService.aceptarPedido(notificacion.id, repartidorId).subscribe({
      next: () => {
        this.successMessage = '¡Pedido aceptado exitosamente!';
        this.loading = false;
        
        // Recargar pedidos disponibles
        setTimeout(() => {
          this.cargarPedidos();
          this.successMessage = null;
        }, 2000);
      },
      error: (err) => {
        console.error('❌ Error al aceptar pedido:', err);
        this.errorMessage = err.error?.message || 'Error al aceptar el pedido. Puede que ya haya sido aceptado por otro repartidor.';
        this.loading = false;
        
        // Recargar pedidos para actualizar estado
        setTimeout(() => this.cargarPedidos(), 2000);
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
}

