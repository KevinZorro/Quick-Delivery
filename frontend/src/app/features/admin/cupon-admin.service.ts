import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
import { environment } from '../../../environments/environment'

export interface CuponGlobalAdmin {
  id: string
  nombre: string
  descripcion: string
  tipo: 'PRIMERA_COMPRA' | 'DESCUENTO_ENVIO' | 'DESCUENTO_PORCENTAJE'
  descuentoPorcentaje: number
  descuentoEnvio: number
  fechaInicio: string | null
  fechaExpiracion: string | null
  usoMaximoTotal: number
  usosActuales: number
  activo: boolean
}

export interface CrearCuponRequest {
  nombre: string
  descripcion: string
  tipo: string
  descuentoPorcentaje: number
  descuentoEnvio: number
  fechaInicio: string | null
  fechaExpiracion: string | null
  usoMaximoTotal: number
  usosActuales: number
  activo: boolean
}

@Injectable({ providedIn: 'root' })
export class CuponAdminService {
  private base = `${environment.edgeApi}/api/cupones-globales`

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<CuponGlobalAdmin[]> {
    return this.http.get<CuponGlobalAdmin[]>(this.base)
  }

  crear(cupon: CrearCuponRequest): Observable<CuponGlobalAdmin> {
    return this.http.post<CuponGlobalAdmin>(this.base, cupon)
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`)
  }
}
