import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HorarioService } from './horarios.service';
import { HorarioAtencion, HorarioAtencionDto } from './horarios.types';
import { AuthService } from '../edge/auth.service';

@Component({
  selector: 'app-horarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './horarios.component.html'
})
export class HorariosComponent implements OnInit {
  horarios: HorarioAtencion[] = [];
  diasSemana = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];
  mostrarModal = false;
  cargando = false;
  mensaje = '';
  error = '';
  restauranteId = '';
  editandoHorarioId: string | null = null;

  get modoEdicion(): boolean {
    return this.editandoHorarioId !== null;
  }

  horarioForm = this.fb.group({
    diaSemana: ['LUNES', Validators.required],
    horaApertura: ['', Validators.required],
    horaCierre: ['', Validators.required],
    activo: [true]
  });

  constructor(
    private fb: FormBuilder,
    private horarioService: HorarioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarRestauranteActual();
  }

  cargarRestauranteActual(): void {
    const usuarioId = this.authService.getUserId();
    if (!usuarioId) {
      this.error = 'No se pudo identificar el usuario autenticado';
      return;
    }

    this.cargando = true;
    this.horarioService.getRestaurantePorUsuario(usuarioId).subscribe({
      next: (restaurante) => {
        this.restauranteId = restaurante.id;
        this.cargarHorarios();
      },
      error: (err) => {
        console.error('Error cargando restaurante:', err);
        this.error = 'Error cargando restaurante';
        this.cargando = false;
      }
    });
  }

  cargarHorarios(): void {
    if (!this.restauranteId) return;

    this.cargando = true;
    this.horarioService.getHorarios(this.restauranteId).subscribe({
      next: (data) => {
        this.horarios = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error cargando horarios:', err);
        this.error = 'Error cargando horarios';
        this.cargando = false;
      }
    });
  }

  abrirModalCrear(): void {
    this.editandoHorarioId = null;
    this.mostrarModal = true;
    this.horarioForm.reset({
      diaSemana: 'LUNES',
      horaApertura: '',
      horaCierre: '',
      activo: true
    });
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.error = '';
    this.editandoHorarioId = null;
  }

  editarHorario(horario: HorarioAtencion): void {
    this.error = '';
    this.editandoHorarioId = horario.id;
    this.mostrarModal = true;

    this.horarioForm.patchValue({
      diaSemana: horario.diaSemana ?? 'LUNES',
      horaApertura: horario.horaApertura ?? '',
      horaCierre: horario.horaCierre ?? '',
      activo: horario.activo ?? true
    });

    this.horarioForm.markAsPristine();
    this.horarioForm.markAsUntouched();
  }

  crearHorario(): void {
    if (!this.restauranteId) {
      this.error = 'No se pudo identificar el restaurante';
      return;
    }

    if (this.horarioForm.invalid) {
      this.error = 'Completa todos los campos';
      return;
    }

    const dto: HorarioAtencionDto = this.horarioForm.value as HorarioAtencionDto;

    if (this.modoEdicion && this.editandoHorarioId) {
      this.horarioService.actualizarHorario(this.restauranteId, this.editandoHorarioId, dto).subscribe({
        next: (horarioActualizado) => {
          this.horarios = this.horarios.map((h) =>
            h.id === horarioActualizado.id ? horarioActualizado : h
          );
          this.mensaje = 'Horario actualizado correctamente';
          this.cerrarModal();
          setTimeout(() => this.mensaje = '', 3000);
        },
        error: (err) => {
          console.error('Error actualizando horario:', err);
          this.error = 'Error actualizando horario';
        }
      });
      return;
    }

    this.horarioService.crearHorario(this.restauranteId, dto).subscribe({
      next: (nuevoHorario) => {
        this.horarios.push(nuevoHorario);
        this.mensaje = 'Horario creado correctamente';
        this.cerrarModal();
        setTimeout(() => this.mensaje = '', 3000);
      },
      error: (err) => {
        console.error('Error creando horario:', err);
        this.error = 'Error creando horario';
      }
    });
  }

  eliminarHorario(horario: HorarioAtencion): void {
    this.horarioService.eliminarHorario(this.restauranteId, horario.id).subscribe({
      next: () => {
        this.horarios = this.horarios.filter((h) => h.id !== horario.id);
        this.mensaje = 'Horario eliminado';
        setTimeout(() => this.mensaje = '', 3000);
      },
      error: (err) => {
        console.error('Error eliminando horario:', err);
        this.error = 'Error eliminando horario';
      }
    });
  }
}
