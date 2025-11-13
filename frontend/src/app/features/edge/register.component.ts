import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../edge/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { passwordValidator } from './validators/password.validator'; // 👈 Importa tu validador

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  selectedRole: string = 'CLIENTE'; // ⭐ Track del rol seleccionado

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      nombre: ['', [Validators.required]],
      correo: ['', [Validators.required, Validators.email]],
      contraseña: ['', [Validators.required, passwordValidator()]], // 👈 Nuevo validador
      telefono: [''],
      rol: ['CLIENTE', [Validators.required]],
      // ⭐ Campos dinámicos para Restaurante
      descripcion: [''],
      categoria: [''],
      // ⭐ Campo dinámico para Delivery
      vehiculo: ['']
    });

    // ⭐ Escuchar cambios en el rol para actualizar validaciones
    this.registerForm.get('rol')?.valueChanges.subscribe((rol) => {
      this.selectedRole = rol;
      this.updateValidators(rol);
    });
  }

  // ⭐ Actualizar validadores según el rol
  updateValidators(rol: string): void {
    const descripcionControl = this.registerForm.get('descripcion');
    const categoriaControl = this.registerForm.get('categoria');
    const vehiculoControl = this.registerForm.get('vehiculo');

    // Limpiar validadores previos
    descripcionControl?.clearValidators();
    categoriaControl?.clearValidators();
    vehiculoControl?.clearValidators();

    // Agregar validadores según el rol
    if (rol === 'RESTAURANTE') {
      descripcionControl?.setValidators([Validators.required]);
      categoriaControl?.setValidators([Validators.required]);
    } else if (rol === 'REPARTIDOR') {
      vehiculoControl?.setValidators([Validators.required]);
    }

    // Actualizar el estado de validación
    descripcionControl?.updateValueAndValidity();
    categoriaControl?.updateValueAndValidity();
    vehiculoControl?.updateValueAndValidity();
  }

  onSubmit(): void {
  this.errorMessage = null;
  this.successMessage = null;

  if (this.registerForm.invalid) {
    Object.keys(this.registerForm.controls).forEach(key => {
      this.registerForm.get(key)?.markAsTouched();
    });
    return;
  }

  this.loading = true;
  const formData = this.registerForm.value;

  // 🧩 Construir el objeto con detalles según el rol
  const payload: any = {
    nombre: formData.nombre,
    correo: formData.correo,
    telefono: formData.telefono,
    contraseña: formData.contraseña,
    rol: formData.rol,
    detalles: {}
  };

  if (formData.rol === 'RESTAURANTE') {
    payload.detalles = {
      descripcion: formData.descripcion,
      categoria: formData.categoria
    };
  } else if (formData.rol === 'REPARTIDOR') {
    payload.detalles = {
      vehiculo: formData.vehiculo
    };
  }

  console.log('📝 Datos de registro:', payload);

  this.authService.register(payload).subscribe({
    next: (res) => {
      this.loading = false;
      this.successMessage = 'Cuenta creada exitosamente. Redirigiendo al login...';
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
    },
    error: (err) => {
      this.loading = false;
      console.error('Error en registro:', err);

      // ⚠️ Aquí detectamos si el backend envió el error del correo duplicado
      if (err.error === 'El correo ya está registrado' || err.error?.message === 'El correo ya está registrado') {
        this.errorMessage = 'Este correo ya está en uso. Por favor usa otro.';
      } else {
        this.errorMessage = 'Error al crear la cuenta. Intenta nuevamente.';
      }
    },
  });
}


  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
