import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from './auth.service';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recuperar-contrasena',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './recuperar-contrasena.component.html'
})
export class RecuperarContrasenaComponent {
  form: FormGroup;
  mensaje: string | null = null;
  error: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.form = this.fb.group({
      correo: ['', [Validators.required, Validators.email]],
    });
  }

enviarSolicitud() {
  this.error = null;
  this.mensaje = null;
  if (this.form.invalid) return;

  this.authService.recuperarContrasena(this.form.value.correo).subscribe({
    next: (res: any) => {
      // Ahora 'res' es un objeto JSON que contiene 'mensaje'
      this.mensaje = res.mensaje || 'Revisa tu correo para el enlace de restablecimiento.';
      this.error = null;
    },
    error: (err) => {
      this.error = err.error?.mensaje || 'Error al enviar la solicitud.';
      this.mensaje = null;
    }
  });
}

}
