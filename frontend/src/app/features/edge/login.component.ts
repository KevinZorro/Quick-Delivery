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
        if (res.rol === 'CLIENTE') {
          this.router.navigate(['/main']);
        } else if (res.rol === 'RESTAURANTE') {
          this.router.navigate(['/restaurante-dashboard']);
        } else if (res.rol === 'DELIVERY') {
          this.router.navigate(['/delivery-dashboard']);
        } else {
          this.errorMessage = 'Rol de usuario desconocido';
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Correo o contraseña incorrectos';
      },
    });
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
