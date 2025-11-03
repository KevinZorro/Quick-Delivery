import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RestauranteService, Restaurante } from './restaurante.service';
import { AuthService } from '../edge/auth.service';
import { HeaderComponent } from './header.component';

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

  constructor(
    private restauranteService: RestauranteService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadRestaurantes();
    // Aquí puedes decodificar el token para obtener el nombre del usuario si lo necesitas
  }

  loadRestaurantes(): void {
    this.restauranteService.getRestaurantes().subscribe({
      next: (data) => {
        this.restaurantes = data;
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
}
