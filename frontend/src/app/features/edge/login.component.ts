import { Component, OnInit } from '@angular/core';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { AuthService } from '../edge/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // Importar RouterModule

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule, // Agrega RouterModule en imports
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      correo: ['', [Validators.required, Validators.email]],
      contraseña: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit(): void {
    this.errorMessage = null;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    const { correo, contraseña } = this.loginForm.value;

    this.authService.login(correo, contraseña).subscribe({
      next: (res) => {
        this.loading = false;

        // El auth.service.ts ya guarda automáticamente: token, userId, nombre y rol
        // Solo guardamos el email que no se guarda en auth.service.ts
        if (res.correo) {
          localStorage.setItem('quick-delivery-userEmail', res.correo);
        }

        console.log('🔐 Login exitoso - Rol recibido:', res.rol);
        console.log('🔐 Respuesta completa:', res);

        // Normalizar el rol para comparación (mayúsculas y sin espacios)
        const rolNormalizado = res.rol?.trim().toUpperCase();

        if (rolNormalizado === 'RESTAURANTE') {
          console.log('🍽️ Redirigiendo a /restaurante/main');
          this.router.navigate(['/restaurante/main']).then(
            (success) => {
              if (success) {
                console.log('✅ Navegación exitosa a /restaurante/main');
              } else {
                console.error('❌ Error en la navegación a /restaurante/main');
              }
            }
          );
        } else if (rolNormalizado === 'CLIENTE') {
          console.log('🛒 Redirigiendo a /main para CLIENTE');
          this.router.navigate(['/main']);
        } else if (rolNormalizado === 'REPARTIDOR' || rolNormalizado === 'DOMICILIARIO') {
          console.log('🚚 Redirigiendo a /delivery/main');
          this.router.navigate(['/delivery/main']);
        } else {
          console.warn('⚠️ Rol desconocido:', res.rol, '- Redirigiendo a /');
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err.error?.message ||
          'Error al iniciar sesión. Verifica tus credenciales.';
        console.error(err);
      },
    });
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
