import { Injectable, Inject, PLATFORM_ID } from '@angular/core'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable } from 'rxjs'
import { environment } from '../../../environments/environment'
import { isPlatformBrowser } from '@angular/common'

export interface CuponGlobal {
  id: string
  nombre: string
  descripcion: string
  tipo: 'PRIMERA_COMPRA' | 'DESCUENTO_ENVIO' | 'DESCUENTO_PORCENTAJE'
  descuentoPorcentaje: number
  descuentoEnvio: number
  fechaExpiracion: string | null
  aplicable: boolean
}

@Injectable({
  providedIn: 'root'
})
export class CuponGlobalService {
  private apiUrl = environment.edgeApi + '/api/cupones-globales'

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: object
  ) {}

  private getHeaders(): HttpHeaders {
    let token = ''
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('quick-delivery-token') || ''
    }
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    })
  }

  obtenerDisponibles(clienteId: string): Observable<CuponGlobal[]> {
    return this.http.get<CuponGlobal[]>(
      `${this.apiUrl}/disponibles?clienteId=${clienteId}`,
      { headers: this.getHeaders() }
    )
  }

  // Selecciona el mejor cupón aplicable (mayor descuento porcentual)
  seleccionarMejor(cupones: CuponGlobal[]): CuponGlobal | null {
    const aplicables = cupones.filter(c => c.aplicable)
    if (!aplicables.length) return null
    return aplicables.reduce((mejor, actual) =>
      actual.descuentoPorcentaje > mejor.descuentoPorcentaje ? actual : mejor
    )
  }

  calcularDescuento(cupon: CuponGlobal, total: number): number {
    if (cupon.tipo === 'DESCUENTO_ENVIO') return cupon.descuentoEnvio
    return Math.round(total * cupon.descuentoPorcentaje / 100)
  }

  etiquetaCupon(cupon: CuponGlobal): string {
    switch (cupon.tipo) {
      case 'PRIMERA_COMPRA': return `${cupon.descuentoPorcentaje}% en tu primera compra`
      case 'DESCUENTO_ENVIO': return `$${cupon.descuentoEnvio} de descuento en envío`
      case 'DESCUENTO_PORCENTAJE': return `${cupon.descuentoPorcentaje}% de descuento`
    }
  }
}
