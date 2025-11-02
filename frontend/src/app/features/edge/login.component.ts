import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../edge/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

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
        
        // Redirigir según el rol del usuario
        if (res.rol === 'RESTAURANTE') {
          this.router.navigate(['/inicio']);
        } else if (res.rol === 'CLIENTE') {
          this.router.navigate(['/dashboard-cliente']);
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

  // ✅ MÉTODO FALTANTE - AGREGADO AQUÍ
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
