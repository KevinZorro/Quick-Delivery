import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DireccionRestauranteService, DireccionRestaurante } from './direcciones-restaurante.service';
import { HeaderComponent } from '../cliente/header.component';

@Component({
  selector: 'app-direccion-restaurante-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HeaderComponent],
  templateUrl: './direcciones-restaurante-form.component.html'
})
export class DireccionRestauranteFormComponent implements OnInit {
  direccionForm!: FormGroup;
  loading = false;
  errorMessage: string | null = null;
  isEditMode = false;
  direccionId: string | null = null;
  usuarioId: string = '';

  tiposReferencia = ['SEDE_PRINCIPAL', 'SUCURSAL', 'COCINA_CENTRAL'];

  constructor(
    private fb: FormBuilder,
    private direccionService: DireccionRestauranteService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // ⭐ Obtener el userId del localStorage (restaurante)
    const restauranteId = localStorage.getItem('quick-delivery-userId');
    
    if (!restauranteId) {
      alert('Error: No se pudo identificar el restaurante. Por favor inicia sesión nuevamente.');
      this.router.navigate(['/login']);
      return;
    }

    this.usuarioId = restauranteId;

    this.direccionForm = this.fb.group({
      calle: ['', Validators.required],
      referencia: [''],
      ciudad: ['', Validators.required],
      barrio: ['', Validators.required],
      coordenadas: [''],
      tipoReferencia: ['SEDE_PRINCIPAL', Validators.required]
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
    
    // ⭐ Construir el request con el userId del localStorage
    const direccionData: DireccionRestaurante = {
      ...this.direccionForm.value,
      usuario: this.usuarioId
    };

    const operation = this.isEditMode && this.direccionId
      ? this.direccionService.actualizarDireccion(this.direccionId, direccionData)
      : this.direccionService.crearDireccion(direccionData);

    operation.subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/restaurante/direcciones']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error al guardar la dirección';
        console.error(err);
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/restaurante/direcciones']);
  }
}
