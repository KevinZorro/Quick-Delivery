import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../edge/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

declare const google: any;

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
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
    // Inicializa el formulario normal
    this.loginForm = this.fb.group({
      correo: ['', [Validators.required, Validators.email]],
      contraseña: ['', [Validators.required, Validators.minLength(6)]],
    });

    // ✅ Inicializa el botón oficial de Google
    google.accounts.id.initialize({
      client_id: '693559539482-8349bkuaaavsu2bj8kcd4nnf38easpg7.apps.googleusercontent.com',
      callback: (response: any) => this.handleGoogleCredential(response)
    });

    google.accounts.id.renderButton(
      document.querySelector('.g_id_signin'),
      { theme: 'outline', size: 'large', width: 250 }
    );
  }

  /**
   * ✅ Cuando Google devuelve el token, se envía al backend para validarlo
   */
  handleGoogleCredential(response: any) {
    const token = response.credential;
    this.loading = true;
    this.errorMessage = null;

    this.authService.loginWithGoogle(token).subscribe({
      next: (res) => {
        this.loading = false;

        // Guardar datos del usuario
        localStorage.setItem('token', res.token);
        localStorage.setItem('quick-delivery-userId', res.userId);
        localStorage.setItem('quick-delivery-userName', res.nombre || res.name || 'Usuario');
        localStorage.setItem('quick-delivery-userRole', res.rol || 'CLIENTE');

        // Redirigir según el rol
        if (res.rol === 'RESTAURANTE') {
          this.router.navigate(['/restaurante/main']);
        } else if (res.rol === 'CLIENTE') {
          this.router.navigate(['/main']);
        } else if (res.rol === 'DOMICILIARIO') {
          this.router.navigate(['/dashboard-domiciliario']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.loading = false;
        console.error('❌ Error al verificar token de Google:', err);
        this.errorMessage = 'Error al iniciar sesión con Google.';
      }
    });
  }

  /**
   * 🔐 Login normal con correo y contraseña
   */
  onSubmit(): void {
    this.errorMessage = null;

    if (this.loginForm.invalid) return;

    this.loading = true;
    const { correo, contraseña } = this.loginForm.value;

    this.authService.login(correo, contraseña).subscribe({
      next: (res) => {
        this.loading = false;
        localStorage.setItem('token', res.token);
        localStorage.setItem('quick-delivery-userId', res.userId);
        localStorage.setItem('quick-delivery-userName', res.nombre || 'Usuario');
        localStorage.setItem('quick-delivery-userRole', res.rol);

        if (res.rol === 'RESTAURANTE') {
          this.router.navigate(['/restaurante/main']);
        } else if (res.rol === 'CLIENTE') {
          this.router.navigate(['/main']);
        } else if (res.rol === 'DOMICILIARIO') {
          this.router.navigate(['/dashboard-domiciliario']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Error al iniciar sesión. Verifica tus credenciales.';
        console.error(err);
      }
    });
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
