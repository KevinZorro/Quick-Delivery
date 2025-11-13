import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../edge/auth.service';


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html'
})
export class HeaderComponent implements OnInit {
  userName: string = 'Usuario';
  userRole: string = '';

  private platformId = inject(PLATFORM_ID);

  constructor(
    private authService: AuthService,
    private router: Router
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
    }
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
}
