import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../edge/auth.service';
import { EliminarCuentaComponent } from '../shared/eliminar-cuenta.component';

@Component({
  selector: 'app-restaurante-perfil',
  standalone: true,
  imports: [CommonModule, EliminarCuentaComponent],
  templateUrl: './perfil.component.html',
})
export class RestaurantePerfilComponent implements OnInit {
  userName: string = '';
  userEmail: string = '';
  userRole: string = '';
  restauranteNombre: string = '';

  private platformId = inject(PLATFORM_ID);

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      if (!this.authService.isLoggedIn()) {
        this.router.navigate(['/login']);
        return;
      }

      this.userName = this.authService.getUserName() || 'Usuario';
      this.userEmail = localStorage.getItem('quick-delivery-userEmail') || '';
      this.userRole = this.authService.getUserRole() || '';
      this.restauranteNombre = localStorage.getItem('quick-delivery-userName') || '';
    }
  }

  volver(): void {
    this.router.navigate(['/restaurante/main']);
  }

  verReportes(): void {
    this.router.navigate(['/dashboard-reportes']);
  }

  verDirecciones(): void {
    this.router.navigate(['/restaurante/direcciones']);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

