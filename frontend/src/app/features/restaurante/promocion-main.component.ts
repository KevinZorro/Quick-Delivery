import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PromocionService } from './promocion.service';
import { PromocionRequest, PromocionResponse } from './promocion.types';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-promocion-main',
    imports: [
    CommonModule,         // <-- Esto habilita ngIf, ngFor y otras directivas
    ReactiveFormsModule
  ],
  standalone: true,
  
  templateUrl: './promocion-main.component.html'
})
export class PromocionMainComponent implements OnInit {
  promocionForm!: FormGroup;
  loading = false;
  mensaje: string | null = null;
    mostrarModal = false;

  promociones: PromocionResponse[] = [];
  cargandoLista = false;
  errorLista: string | null = null;

  constructor(
    private fb: FormBuilder,
    private promocionService: PromocionService
  ) {}

  ngOnInit(): void {
    this.promocionForm = this.fb.group({
      codigo: ['', [Validators.required, Validators.maxLength(20)]],
      descuentoPorcentaje: [10, [Validators.required, Validators.min(1), Validators.max(100)]],
      fechaExpiracion: ['', Validators.required],
      estado: ['DISPONIBLE', Validators.required],
      activo: [true]
    });

    this.cargarMisPromociones();
  }



abrirModalCrear(): void {
  this.promocionForm.reset({
    descuentoPorcentaje: 10,
    estado: 'ACTIVA',
    activo: true,
    cantidadUsos: 0
  });
  this.mensaje = null;
  this.mostrarModal = true;
}

cerrarModal(): void {
  this.mostrarModal = false;
}

  cargarMisPromociones(): void {
    this.cargandoLista = true;
    this.errorLista = null;

    this.promocionService.getMisPromociones().subscribe({
      next: (data) => {
        this.promociones = data;
        this.cargandoLista = false;
      },
      error: (err) => {
        console.error(err);
        this.errorLista = 'Error al cargar las promociones';
        this.cargandoLista = false;
      }
    });
  }

  crearPromocion(): void {
    if (this.promocionForm.invalid) return;
    this.loading = true;
    this.mensaje = null;

    const promocion: PromocionRequest = this.promocionForm.value;
    this.promocionService.crearPromocion(promocion).subscribe({
      next: () => {
        this.mensaje = 'Promoción creada exitosamente';
        this.promocionForm.reset({ descuentoPorcentaje: 10, estado: 'DISPONIBLE', activo: true });
        this.loading = false;
        this.cargarMisPromociones(); // recargar lista
      },
      error: err => {
        this.mensaje = 'Error al crear la promoción';
        console.error(err);
        this.loading = false;
      }
    });
  }

  cambiarEstado(promo: PromocionResponse, nuevoEstado: string) {
  this.promocionService.updatePromocion(promo.id, { estado: nuevoEstado }).subscribe({
    next: () => this.cargarMisPromociones(),
    error: err => console.error(err)
  });
}

actualizarCantidadUsos(promo: PromocionResponse, cantidad: number) {
  this.promocionService.updatePromocion(promo.id, { cantidadUsos: cantidad }).subscribe({
    next: () => this.cargarMisPromociones(),
    error: err => console.error(err)
  });
}

cambiarFechaExpiracion(promo: PromocionResponse, nuevaFecha: string) {
  // nuevaFecha en formato 'YYYY-MM-DD'
  this.promocionService.updatePromocion(promo.id, { fechaExpiracion: nuevaFecha }).subscribe({
    next: () => this.cargarMisPromociones(),
    error: err => console.error(err)
  });
}


}
