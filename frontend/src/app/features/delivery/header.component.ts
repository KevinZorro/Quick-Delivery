import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../edge/auth.service';

@Component({
  selector: 'app-delivery-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html'
})
export class DeliveryHeaderComponent implements OnInit {
  userName: string = 'Repartidor';
  userRole: string = '';

  private platformId = inject(PLATFORM_ID);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
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
    localStorage.removeItem('quick-delivery-token');
    localStorage.removeItem('quick-delivery-userId');
    localStorage.removeItem('quick-delivery-userName');
    localStorage.removeItem('quick-delivery-userRole');
    
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

