import { Component, OnDestroy, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../edge/auth.service';
import { NotificacionEstadoPedido, NotificacionesPedidoService } from './notificaciones-pedido.service';


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html'
})
export class HeaderComponent implements OnInit, OnDestroy {
  userName: string = 'Usuario';
  userRole: string = '';
  notificacionPedido: NotificacionEstadoPedido | null = null;
  private notificacionesSub?: Subscription;

  private platformId = inject(PLATFORM_ID);

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificacionesPedidoService: NotificacionesPedidoService
  ) {}


  ngOnInit(): void {
    // ⭐ Obtener el nombre del usuario desde localStorage si lo guardaste
    if (isPlatformBrowser(this.platformId)) {
      const userName = localStorage.getItem('quick-delivery-userName');
      if (userName) {
        this.userName = userName;
      }
      
      const userRole = localStorage.getItem('quick-delivery-userRole');
      if (userRole) {
        this.userRole = userRole;
      }

      this.conectarNotificacionesPedido();
    }
  }

  ngOnDestroy(): void {
    this.notificacionesSub?.unsubscribe();
    this.notificacionesPedidoService.cerrar();
  }


  logout(): void {
    // ⭐ Limpiar localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('quick-delivery-userId');
    localStorage.removeItem('quick-delivery-userName');
    localStorage.removeItem('quick-delivery-userRole');
    
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  cerrarNotificacionPedido(): void {
    this.notificacionPedido = null;
  }

  private conectarNotificacionesPedido(): void {
    const usuarioId = this.authService.getUserId();
    if (!usuarioId || this.authService.getUserRole() !== 'CLIENTE') {
      return;
    }

    this.notificacionesPedidoService.pedirPermisoBrowser();
    this.notificacionesSub = this.notificacionesPedidoService.conectar(usuarioId).subscribe({
      next: (notificacion) => {
        this.notificacionPedido = notificacion;
        this.notificacionesPedidoService.mostrarBrowser(notificacion);
        setTimeout(() => this.notificacionPedido = null, 7000);
      },
      error: (error) => {
        console.error('Error en notificaciones de pedidos:', error);
      }
    });
  }
}
