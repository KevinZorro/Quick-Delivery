import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';

export interface PedidoDisponible {
  id: string;
  restauranteId: string;
  direccionEntregaId: string;
  total: number;
  estado: string;
  fechaCreacion: string;
  direccionEntrega: string;
  coordenadasEntrega: string;
  distanciaKm: number;
  distanciaTexto: string;
  tiempoEstimado: string;
  items: ItemPedidoDisponible[];
}

export interface ItemPedidoDisponible {
  id: string;
  productoId: string;
  cantidad: number;
  precioUnidad: number;
  subtotal: number;
}

export interface NotificacionPedido {
  id: string;
  pedidoId: string;
  restauranteId: string;
  clienteId: string;
  total: number;
  fechaCreacion: string;
  direccionRestaurante: string;
  coordenadasRestaurante: string;
  distanciaKm: number;
  distanciaTexto: string;
  tiempoEstimado: string;
}

export interface Entrega {
  id: string;
  clienteId: string;
  codigoEntrega: string;
  comentario?: string;
  estado: 'EN_CAMINO_RECOGIDO' | 'EN_CAMINO_HACIA_CLIENTE' | 'ENTREGADO';
  pedidoId: string;
  repartidorId: string;
  fechaCreacion: string;
  fechaActualizacion?: string;
  cliente?: {
    id: string;
    usuarioId: string;
  };
  restauranteId?: string;
  direccionEntregaId?: string;
  total?: number;
  metodoPago?: string;
  preferencias?: string;
  items?: ItemPedido[];
}

export interface ItemPedido {
  id: string;
  productoId: string;
  cantidad: number;
  precioUnidad: number;
  subtotal: number;
}

export interface PedidoCompleto {
  pedido: {
    id: string;
    cliente: {
      id: string;
      usuarioId: string;
    };
    restauranteId: string;
    repartidorId: string;
    direccionEntregaId: string;
    total: number;
    metodoPago: string;
    estado: string;
    preferencias: string;
    fechaCreacion: string;
    fechaActualizacion: string;
    items: ItemPedido[];
  };
  cliente: {
    id: string;
    nombre: string;
    telefono: string;
  };
  direccionEntrega: {
    id: string;
    usuarioId: string;
    calle: string;
    referencia: string;
    ciudad: string;
    barrio: string;
    coordenadas: string;
    tipoReferencia: string;
  };
  productos: Producto[];
}

export interface Producto {
  id: string;
  restauranteId: string;
  nombre: string;
  descripcion: string;
  precio: number;
  categoria: string;
  disponible: boolean;
  imagenUrl: string;
  fechaCreacion: string;
  fechaActualizacion: string;
}

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private baseUrl = environment.deliveryApi + '/api/delivery';

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

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

  obtenerPedidosDisponibles(usuarioId: string): Observable<PedidoDisponible[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<PedidoDisponible[]>(
      `${this.baseUrl}/pedidos/disponibles?usuarioId=${usuarioId}`,
      { headers }
    );
  }

  aceptarPedido(pedidoId: string, usuarioId: string): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.post<void>(
      `${this.baseUrl}/pedidos/${pedidoId}/aceptar?usuarioId=${usuarioId}`,
      null,
      { headers }
    );
  }

  actualizarUbicacion(usuarioId: string, latitud: number, longitud: number, rangoKm?: number): Observable<any> {
    const headers = this.getAuthHeaders();
    let url = `${this.baseUrl}/${usuarioId}/ubicacion?latitud=${latitud}&longitud=${longitud}`;
    if (rangoKm) {
      url += `&rangoKm=${rangoKm}`;
    }
    return this.http.patch<any>(url, null, { headers });
  }

  // Notificaciones
  obtenerNotificacionesDisponibles(usuarioId: string): Observable<NotificacionPedido[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<NotificacionPedido[]>(
      `${this.baseUrl}/notificaciones/disponibles?usuarioId=${usuarioId}`,
      { headers }
    );
  }

  aceptarNotificacion(usuarioId: string, notificacionId: string, comentario?: string): Observable<Entrega> {
    const headers = this.getAuthHeaders();
    return this.http.post<Entrega>(
      `${this.baseUrl}/entregas/aceptar?usuarioId=${usuarioId}&notificacionId=${notificacionId}`,
      { comentario: comentario || '' },
      { headers }
    );
  }

  // Entregas
 listarEntregas(usuarioId: string): Observable<Entrega[]> {
  const headers = this.getAuthHeaders();
  return this.http.get<Entrega[]>(
    `${this.baseUrl}/historial?usuarioId=${usuarioId}`,
    { headers }
  );
}

  actualizarEstadoEntrega(entregaId: string, estado: 'EN_CAMINO_RECOGIDO' | 'EN_CAMINO_HACIA_CLIENTE' | 'ENTREGADO'): Observable<Entrega> {
    const headers = this.getAuthHeaders();
    return this.http.patch<Entrega>(
      `${this.baseUrl}/entregas/${entregaId}/estado?estado=${estado}`,
      null,
      { headers }
    );
  }

  obtenerPedidoCompleto(pedidoId: string): Observable<PedidoCompleto> {
    const headers = this.getAuthHeaders();
    return this.http.get<PedidoCompleto>(
      `${this.baseUrl}/pedido/completo/${pedidoId}`,
      { headers }
    );
  }
}
