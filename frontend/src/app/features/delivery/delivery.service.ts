import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { isPlatformBrowser } from '@angular/common';

export interface PedidoNotificacion {
  id: string;
  pedidoId: string;
  clienteId: string;
  restauranteId: string;
  direccionEntregaId: string;
  estado: 'PENDIENTE' | 'ACEPTADO' | 'RECHAZADO';
  repartidorId?: string;
  fechaCreacion: string;
  fechaAceptacion?: string;
}

export interface PedidoNotificacionConDistancia extends PedidoNotificacion {
  distanciaKm: number;
  distanciaMetros: number;
  distanciaFormateada: string;
  duracionEstimada: string;
}

export interface AceptarPedidoRequest {
  repartidorId: string;
}

export interface UbicacionGPS {
  latitud: number;
  longitud: number;
  rangoMaximoKm?: number;
}

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private baseUrl = environment.deliveryApi + '/api/pedidos-notificaciones';

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    console.log('Base URL para DeliveryService:', this.baseUrl);
  }

  private getAuthHeaders(): HttpHeaders {
    let token = '';
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('quick-delivery-token') || '';
    }
    
    if (token) {
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
    }
    
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  /**
   * Obtener pedidos pendientes filtrados por ubicación GPS
   */
  obtenerPedidosPendientesPorUbicacion(
    latitud: number,
    longitud: number,
    rangoMaximoKm: number = 10.0
  ): Observable<PedidoNotificacionConDistancia[]> {
    const headers = this.getAuthHeaders();
    const params = new HttpParams()
      .set('lat', latitud.toString())
      .set('lng', longitud.toString())
      .set('rangoMaximoKm', rangoMaximoKm.toString());

    console.log('📍 Obteniendo pedidos pendientes por ubicación:', { latitud, longitud, rangoMaximoKm });
    
    return this.http.get<PedidoNotificacionConDistancia[]>(
      `${this.baseUrl}/pendientes-por-ubicacion`,
      { headers, params }
    );
  }

  /**
   * Obtener todas las notificaciones pendientes (sin filtro de ubicación)
   */
  obtenerPedidosPendientes(): Observable<PedidoNotificacion[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<PedidoNotificacion[]>(
      `${this.baseUrl}/pendientes`,
      { headers }
    );
  }

  /**
   * Aceptar un pedido
   */
  aceptarPedido(notificacionId: string, repartidorId: string): Observable<PedidoNotificacion> {
    const headers = this.getAuthHeaders();
    const request: AceptarPedidoRequest = { repartidorId };
    
    console.log('✅ Aceptando pedido:', { notificacionId, repartidorId });
    
    return this.http.post<PedidoNotificacion>(
      `${this.baseUrl}/${notificacionId}/aceptar`,
      request,
      { headers }
    );
  }

  /**
   * Obtener pedidos aceptados por un repartidor
   */
  obtenerPedidosAceptados(repartidorId: string): Observable<PedidoNotificacion[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<PedidoNotificacion[]>(
      `${this.baseUrl}/repartidor/${repartidorId}/aceptados`,
      { headers }
    );
  }

  /**
   * Obtener una notificación por ID de pedido
   */
  obtenerNotificacionPorPedidoId(pedidoId: string): Observable<PedidoNotificacion> {
    const headers = this.getAuthHeaders();
    return this.http.get<PedidoNotificacion>(
      `${this.baseUrl}/pedido/${pedidoId}`,
      { headers }
    );
  }
}

