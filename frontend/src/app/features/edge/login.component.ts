import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../edge/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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
        
        // ⭐ Guardar información del usuario en localStorage PARA TODOS LOS ROLES
        localStorage.setItem('token', res.token);
        localStorage.setItem('quick-delivery-userId', res.userId);
        localStorage.setItem('quick-delivery-userName', res.nombre || 'Usuario');
        localStorage.setItem('quick-delivery-userRole', res.rol);
        
        // Redirigir según el rol del usuario
        if (res.rol === 'RESTAURANTE') {
          this.router.navigate(['/restaurante/main']);
        } else if (res.rol === 'CLIENTE') {
          this.router.navigate(['/main']);
          console.log('Navegando a /main para CLIENTE');
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
