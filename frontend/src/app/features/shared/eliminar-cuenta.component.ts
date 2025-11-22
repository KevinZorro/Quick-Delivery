import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../edge/auth.service';

@Component({
  selector: 'app-eliminar-cuenta',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './eliminar-cuenta.component.html',
})
export class EliminarCuentaComponent {
  @Input() userRole: string = '';

  contrasena: string = '';
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  eliminarCuenta(): void {
    if (!this.contrasena || this.contrasena.trim() === '') {
      this.errorMessage = 'Por favor ingresa tu contraseña';
      return;
    }

    const confirmacion = confirm(
      '¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.'
    );

    if (!confirmacion) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    this.authService.eliminarMiCuenta(this.contrasena).subscribe({
      next: () => {
        this.successMessage = 'Cuenta eliminada exitosamente';
        setTimeout(() => {
          this.authService.logout();
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 401) {
          this.errorMessage = 'Contraseña incorrecta';
        } else {
          this.errorMessage =
            'Error al eliminar la cuenta. Por favor intenta nuevamente.';
        }
        this.contrasena = '';
      },
    });
  }
}
