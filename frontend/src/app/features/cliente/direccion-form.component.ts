import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { DireccionService, Direccion } from './direccion.service';
import { HeaderComponent } from './header.component';


@Component({
  selector: 'app-direccion-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HeaderComponent],
  templateUrl: './direccion-form.component.html'
})
export class DireccionFormComponent implements OnInit {
  direccionForm!: FormGroup;
  loading = false;
  errorMessage: string | null = null;
  isEditMode = false;
  direccionId: string | null = null;
  usuarioId: string = '';

  private platformId = inject(PLATFORM_ID);

  tiposReferencia = ['CASA', 'TRABAJO', 'OTRO'];

  private readonly callePattern = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9\s\-\.\#]{3,100}$/;
  private readonly nombrePattern = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]{3,100}$/;

  constructor(
    private fb: FormBuilder,
    private direccionService: DireccionService,
    private router: Router,
    private route: ActivatedRoute
  ) {}


  ngOnInit(): void {
    // ⭐ Obtener el userId del localStorage SOLO en el navegador
    if (isPlatformBrowser(this.platformId)) {
      const clienteId = localStorage.getItem('quick-delivery-userId');
      
      if (!clienteId) {
        alert('Error: No se pudo identificar el usuario. Por favor inicia sesión nuevamente.');
        this.router.navigate(['/login']);
        return;
      }

      this.usuarioId = clienteId;
    }

    this.direccionForm = this.fb.group({
      calle: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(100),
        Validators.pattern(this.callePattern)
      ]],
      referencia: [''],
      ciudad: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(100),
        Validators.pattern(this.nombrePattern)
      ]],
      barrio: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(100),
        Validators.pattern(this.nombrePattern)
      ]],
      coordenadas: [''],
      tipoReferencia: ['CASA', Validators.required]
    });

    // Verificar si es modo edición
    this.direccionId = this.route.snapshot.paramMap.get('id');
    if (this.direccionId) {
      this.isEditMode = true;
      this.loadDireccion();
    }
  }


  loadDireccion(): void {
    if (this.direccionId) {
      this.direccionService.getDireccionById(this.direccionId).subscribe({
        next: (direccion) => {
          this.direccionForm.patchValue(direccion);
        },
        error: (err) => {
          this.errorMessage = 'Error al cargar la dirección';
          console.error(err);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.direccionForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    
    // ⭐ Construir el request con el userId del localStorage
    const direccionData: Direccion = {
      ...this.direccionForm.value,
      usuario: this.usuarioId
    };

    const operation = this.isEditMode && this.direccionId
      ? this.direccionService.actualizarDireccion(this.direccionId, direccionData)
      : this.direccionService.crearDireccion(direccionData);

    operation.subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/cliente/direcciones']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err);
        console.error(err);
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/cliente/direcciones']);
  }

  private getErrorMessage(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Intenta nuevamente más tarde.';
    }

    if (error.error) {
      if (typeof error.error === 'string') {
        return error.error;
      }

      if (error.error.message) {
        return error.error.message;
      }

      if (error.error.errors) {
        const firstError = Object.values(error.error.errors)[0];
        if (Array.isArray(firstError)) {
          return firstError[0] as string;
        }
        return firstError as string;
      }
    }

    return 'Error al guardar la dirección. Por favor verifica los datos e intenta nuevamente.';
  }
}
