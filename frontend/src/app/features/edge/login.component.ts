import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../edge/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';  // Importar RouterModule

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule  // Agrega RouterModule en imports
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
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
