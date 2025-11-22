import { Component, OnInit, OnDestroy, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DeliveryService, NotificacionPedido } from './delivery.service';
import { AuthService } from '../edge/auth.service';

@Component({
  selector: 'app-delivery-main',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './main.component.html'
})
export class DeliveryMainComponent implements OnInit, OnDestroy {
  notificaciones: NotificacionPedido[] = [];
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  userName: string = '';
  usuarioId: string = '';
  ubicacionActualizada = false;
  rangoKm: number = 10;
  mostrarConfigRango = false;
  intervaloNotificaciones: any;

  private platformId = inject(PLATFORM_ID);

  constructor(
    private deliveryService: DeliveryService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.usuarioId = this.authService.getUserId() || '';
      this.userName = this.authService.getUserName() || '';

      if (!this.authService.isRepartidor()) {
        this.router.navigate(['/login']);
        return;
      }

      // ⭐ Usar AuthService para verificar autenticación (usa la clave correcta del token)
      if (!this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }

      // Solicitar ubicación GPS
      this.obtenerUbicacion();
      
      // Cargar notificaciones cada 10 segundos
      this.intervaloNotificaciones = setInterval(() => {
        this.cargarNotificaciones();
      }, 10000);
    }
  }

  ngOnDestroy(): void {
    if (this.intervaloNotificaciones) {
      clearInterval(this.intervaloNotificaciones);
    }
  }

  obtenerUbicacion(): void {
    if (!navigator.geolocation) {
      this.errorMessage = 'Tu navegador no soporta geolocalización';
      return;
    }

    this.loading = true;
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const latitud = position.coords.latitude;
        const longitud = position.coords.longitude;
        
        // Actualizar ubicación en el backend
        this.deliveryService.actualizarUbicacion(this.usuarioId, latitud, longitud, this.rangoKm).subscribe({
          next: () => {
            this.ubicacionActualizada = true;
            this.cargarNotificaciones();
          },
          error: (err) => {
            console.error('Error al actualizar ubicación:', err);
            // Aún así, intentar cargar notificaciones (puede que ya tenga ubicación guardada)
            this.cargarNotificaciones();
          }
        });
      },
      (error) => {
        this.errorMessage = 'Error al obtener tu ubicación: ' + error.message;
        this.loading = false;
        // Aún así, intentar cargar notificaciones (puede que ya tenga ubicación guardada)
        this.cargarNotificaciones();
      }
    );
  }

  cargarNotificaciones(): void {
    this.errorMessage = null;

    this.deliveryService.obtenerNotificacionesDisponibles(this.usuarioId).subscribe({
      next: (notificaciones) => {
        this.notificaciones = notificaciones;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar notificaciones';
        console.error(err);
        this.loading = false;
      }
    });
  }

  aceptarNotificacion(notificacion: NotificacionPedido): void {
    const comentario = prompt(`¿Aceptar el pedido #${notificacion.pedidoId.substring(0, 8)} por $${notificacion.total}?\n\nComentario (opcional):`);
    if (comentario === null) {
      return; // Usuario canceló
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.deliveryService.aceptarNotificacion(this.usuarioId, notificacion.id, comentario || undefined).subscribe({
      next: (entrega) => {
        this.successMessage = `Pedido aceptado exitosamente. Código de entrega: ${entrega.codigoEntrega}`;
        this.cargarNotificaciones(); // Recargar lista
        setTimeout(() => this.successMessage = null, 5000);
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al aceptar el pedido';
        console.error(err);
        this.loading = false;
      }
    });
  }

  actualizarRangoKm(): void {
    this.loading = true;
    this.deliveryService.actualizarUbicacion(this.usuarioId, 0, 0, this.rangoKm).subscribe({
      next: () => {
        this.successMessage = `Rango actualizado a ${this.rangoKm} km`;
        this.mostrarConfigRango = false;
        this.cargarNotificaciones();
        setTimeout(() => this.successMessage = null, 3000);
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al actualizar rango';
        console.error(err);
        this.loading = false;
      }
    });
  }

  verEntregas(): void {
    this.router.navigate(['/delivery/entregas']);
  }

  verPerfil(): void {
    this.router.navigate(['/delivery/perfil']);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  actualizarUbicacion(): void {
    this.obtenerUbicacion();
  }
}

