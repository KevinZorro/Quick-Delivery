import { Component, OnInit } from '@angular/core'
import { CommonModule } from '@angular/common'
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms'
import { Router } from '@angular/router'
import { AuthService } from '../edge/auth.service'
import { CuponAdminService, CuponGlobalAdmin } from './cupon-admin.service'

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin.component.html',
})
export class AdminComponent implements OnInit {
  cupones: CuponGlobalAdmin[] = []
  cargando = true
  error: string | null = null
  exito: string | null = null

  mostrarModal = false
  guardando = false

  cuponForm!: FormGroup

  constructor(
    private authService: AuthService,
    private cuponService: CuponAdminService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/login'])
      return
    }
    this.iniciarFormulario()
    this.cargarCupones()
  }

  private iniciarFormulario(): void {
    this.cuponForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: ['', Validators.required],
      tipo: ['DESCUENTO_PORCENTAJE', Validators.required],
      descuentoPorcentaje: [0, [Validators.min(0), Validators.max(100)]],
      descuentoEnvio: [0, Validators.min(0)],
      fechaInicio: [this.hoy()],
      fechaExpiracion: [null],
      // 0 = ilimitado; cualquier número positivo = límite de usos
      usoMaximoTotal: [0, Validators.min(0)],
    })
  }

  private hoy(): string {
    return new Date().toISOString().split('T')[0]
  }

  cargarCupones(): void {
    this.cargando = true
    this.cuponService.listarTodos().subscribe({
      next: (data) => {
        this.cupones = data
        this.cargando = false
      },
      error: () => {
        this.error = 'Error al cargar cupones'
        this.cargando = false
      },
    })
  }

  abrirModal(): void {
    this.cuponForm.reset({
      nombre: '',
      descripcion: '',
      tipo: 'DESCUENTO_PORCENTAJE',
      descuentoPorcentaje: 0,
      descuentoEnvio: 0,
      fechaInicio: this.hoy(),
      fechaExpiracion: null,
      usoMaximoTotal: 0,
    })
    this.error = null
    this.exito = null
    this.mostrarModal = true
  }

  cerrarModal(): void {
    this.mostrarModal = false
  }

  guardarCupon(): void {
    if (this.cuponForm.invalid) return
    this.guardando = true
    this.error = null

    const val = this.cuponForm.value
    this.cuponService.crear({
      nombre: val.nombre,
      descripcion: val.descripcion,
      tipo: val.tipo,
      descuentoPorcentaje: val.descuentoPorcentaje ?? 0,
      descuentoEnvio: val.descuentoEnvio ?? 0,
      fechaInicio: val.fechaInicio || null,
      fechaExpiracion: val.fechaExpiracion || null,
      usoMaximoTotal: val.usoMaximoTotal ?? 0,
      usosActuales: 0,
      activo: true,
    }).subscribe({
      next: () => {
        this.guardando = false
        this.mostrarModal = false
        this.exito = 'Cupón creado correctamente'
        this.cargarCupones()
        setTimeout(() => (this.exito = null), 3000)
      },
      error: (err) => {
        this.guardando = false
        this.error = err.error?.error || 'Error al crear el cupón'
      },
    })
  }

  eliminarCupon(id: string, nombre: string): void {
    if (!confirm(`¿Eliminar el cupón "${nombre}"? Esta acción no se puede deshacer.`)) return
    this.cuponService.eliminar(id).subscribe({
      next: () => {
        this.exito = `Cupón "${nombre}" eliminado`
        this.cargarCupones()
        setTimeout(() => (this.exito = null), 3000)
      },
      error: () => {
        this.error = 'Error al eliminar el cupón'
      },
    })
  }

  cerrarSesion(): void {
    this.authService.logout()
    this.router.navigate(['/login'])
  }

  // Helpers para la vista
  etiquetaTipo(tipo: string): string {
    const mapa: Record<string, string> = {
      PRIMERA_COMPRA: 'Primera compra',
      DESCUENTO_ENVIO: 'Descuento envío',
      DESCUENTO_PORCENTAJE: 'Descuento %',
    }
    return mapa[tipo] ?? tipo
  }

  colorTipo(tipo: string): string {
    const mapa: Record<string, string> = {
      PRIMERA_COMPRA: 'bg-purple-100 text-purple-700',
      DESCUENTO_ENVIO: 'bg-blue-100 text-blue-700',
      DESCUENTO_PORCENTAJE: 'bg-green-100 text-green-700',
    }
    return mapa[tipo] ?? 'bg-gray-100 text-gray-700'
  }

  usosTexto(cupon: CuponGlobalAdmin): string {
    if (cupon.usoMaximoTotal === 0) return `${cupon.usosActuales} / ilimitado`
    return `${cupon.usosActuales} / ${cupon.usoMaximoTotal}`
  }

  estaAgotado(cupon: CuponGlobalAdmin): boolean {
    return cupon.usoMaximoTotal > 0 && cupon.usosActuales >= cupon.usoMaximoTotal
  }

  get tipoSeleccionado(): string {
    return this.cuponForm.get('tipo')?.value ?? ''
  }
}
