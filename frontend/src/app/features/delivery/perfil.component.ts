import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../edge/auth.service';
import { HeaderComponent } from './header.component';
import { EliminarCuentaComponent } from '../shared/eliminar-cuenta.component';

@Component({
  selector: 'app-delivery-perfil',
  standalone: true,
  imports: [CommonModule, HeaderComponent, EliminarCuentaComponent],
  templateUrl: './perfil.component.html',
})
export class DeliveryPerfilComponent implements OnInit {
  userName: string = '';
  userEmail: string = '';
  userRole: string = '';

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
    }
  }
}

