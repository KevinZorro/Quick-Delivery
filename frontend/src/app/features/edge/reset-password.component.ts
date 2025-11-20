import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../edge/auth.service';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './reset-password.component.html'
})
export class ResetPasswordComponent implements OnInit {
  form: FormGroup;
  token: string | null = null;
  tokenValido = false;
  mensaje: string | null = null;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      nuevaContrasena: ['', [Validators.required, Validators.minLength(6)]],
      confirmarContrasena: ['', [Validators.required]]
    }, { validators: this.passwordsIguales });
  }

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token');
    console.log('Token recibido en frontend:', this.token);

    if (this.token) {
      this.authService.validarToken(this.token).subscribe({
        next: (res: any) => {
          console.log('Respuesta validación token:', res);
          if (res.valido) {
            this.tokenValido = true;
            this.error = null;
          } else {
            this.tokenValido = false;
            this.error = res.mensaje || 'Token inválido o expirado.';
          }
        },
        error: (err) => {
          console.error('Error validando token:', err);
          this.tokenValido = false;
          this.error = 'Token inválido o expirado.';
        }
      });
    } else {
      console.warn('Token no encontrado en URL');
      this.tokenValido = false;
      this.error = 'Token no encontrado.';
    }
  }

  passwordsIguales(group: FormGroup) {
    return group.get('nuevaContrasena')?.value === group.get('confirmarContrasena')?.value
      ? null : { noIguales: true };
  }

  cambiarContrasena() {
    if (this.form.invalid || !this.token) return;

    const nuevaContrasena = this.form.value.nuevaContrasena;
    this.authService.cambiarContrasena(this.token, nuevaContrasena).subscribe({
      next: (res: any) => {
        this.mensaje = res.mensaje || 'Contraseña actualizada. Puedes iniciar sesión.';
        this.error = null;
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: err => {
        this.error = err.error?.mensaje || 'Error al cambiar la contraseña.';
        this.mensaje = null;
      }
    });
  }
}
