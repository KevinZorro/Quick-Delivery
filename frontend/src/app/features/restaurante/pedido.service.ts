import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { isPlatformBrowser } from '@angular/common';

export interface ItemPedido {
  id: string;
  productoId: string;
  cantidad: number;
  precioUnidad: number;
  subtotal: number;
}

export interface Pedido {
  id: string;
  clienteId: string;
  restauranteId: string;
  repartidorId?: string;
  direccionEntregaId?: string;
  total: number;
  estado: string;
  metodoPago?: string;
  fechaCreacion: string;
  fechaActualizacion?: string;
  preferencias?: string;
  items?: ItemPedido[];
}

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = environment.restaurantesApi + '/api/restaurante';

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    console.log('Base URL para PedidoService (Restaurante):', this.apiUrl);
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
   * Obtener pedidos pendientes del restaurante autenticado
   * GET /api/restaurante/pedidos/pendientes
   */
  obtenerPedidosPendientes(): Observable<Pedido[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Pedido[]>(
      `${this.apiUrl}/pedidos/pendientes`,
      { headers }
    );
  }

  /**
   * Aceptar un pedido
   * POST /api/restaurante/pedido/{id}/aceptar
   */
  aceptarPedido(pedidoId: string): Observable<Pedido> {
    const headers = this.getAuthHeaders();
    return this.http.post<Pedido>(
      `${this.apiUrl}/pedido/${pedidoId}/aceptar`,
      null,
      { headers }
    );
  }

  /**
   * Rechazar un pedido
   * POST /api/restaurante/pedido/{id}/rechazar
   */
  rechazarPedido(pedidoId: string): Observable<Pedido> {
    const headers = this.getAuthHeaders();
    return this.http.post<Pedido>(
      `${this.apiUrl}/pedido/${pedidoId}/rechazar`,
      null,
      { headers }
    );
  }
}

