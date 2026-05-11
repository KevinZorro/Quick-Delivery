import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RestauranteService, Restaurante } from './restaurante.service';
import { AuthService } from '../edge/auth.service';
import { HeaderComponent } from './header.component';
import { CuponGlobalService, CuponGlobal } from './cupon-global.service';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [CommonModule, HeaderComponent],
  templateUrl: './main.component.html'
})
export class MainComponent implements OnInit {
  restaurantes: Restaurante[] = [];
  loading = true;
  errorMessage: string | null = null;
  userName: string = '';
  cuponesDisponibles: CuponGlobal[] = [];

  constructor(
    private restauranteService: RestauranteService,
    private router: Router,
    private authService: AuthService,
    public cuponGlobalService: CuponGlobalService
  ) {}

  ngOnInit(): void {
    this.loadRestaurantes();
    this.loadCupones();
  }

  loadCupones(): void {
    const clienteId = this.authService.getUserId();
    if (!clienteId) return;
    this.cuponGlobalService.obtenerDisponibles(clienteId).subscribe({
      next: (cupones) => {
        this.cuponesDisponibles = cupones.filter(c => c.aplicable);
      },
      error: () => {}
    });
  }

  loadRestaurantes(): void {
    this.restauranteService.getRestaurantes().subscribe({
      next: (data) => {
        // Ordenar restaurantes: abiertos primero, luego cerrados
        this.restaurantes = data.sort((a, b) => {
          if (a.disponible && !b.disponible) return -1;
          if (!a.disponible && b.disponible) return 1;
          return 0;
        });
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar restaurantes';
        this.loading = false;
      }
    });
  }

  selectRestaurante(id: string): void {
    sessionStorage.setItem('selectedRestauranteId', id);
    this.router.navigate(['/restaurante', id]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  formatearCalificacion(calificacion: any): string {
    const num = parseFloat(calificacion);
    if (!calificacion || isNaN(num)) return 'N/A';
    return num.toFixed(1);
  }
}