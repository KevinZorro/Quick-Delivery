// src/app/features/pedidos/pedidos.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { isPlatformBrowser } from '@angular/common';

export interface PedidoDto {
  id: string;
  restauranteId: string;
  clienteId?: string;
  total: number;
  fechaCreacion: string;
  estado: string;
  metodoPago?: string;
  preferencias?: string;
  tiempoEstimado?: number;
  items: ItemPedidoDto[] | null;
}

export interface ItemPedidoDto {
  productoId: string;
  nombreProducto?: string;
  categoria?: string;
  imagenUrl?: string;
  cantidad: number;
  precioUnidad: number;
  subtotal: number;
}

@Injectable({ providedIn: 'root' })
export class PedidosService {

  private restaurantesBaseUrl = environment.restaurantesApi + '/api/restaurante';
  private clientesBaseUrl = environment.clientesApi + '/api/pedidos';

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  private getHeaders(): HttpHeaders {
    let token = '';
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('quick-delivery-token') || '';
    }
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getHistorial(restauranteId: string): Observable<PedidoDto[]> {
    return this.http.get<PedidoDto[]>(
      `${this.restaurantesBaseUrl}/${restauranteId}/historial-completo`,
      { headers: this.getHeaders() }
    );
  }

  actualizarEstado(pedidoId: string, nuevoEstado: string) {
    return this.http.put(
      `${this.restaurantesBaseUrl}/pedidos/${pedidoId}/estado`,
      null,
      { params: { nuevoEstado }, headers: this.getHeaders() }
    );
  }

  /**
   * Aceptar un pedido nuevo
   */
  aceptarPedido(pedidoId: string): Observable<PedidoDto> {
    return this.http.post<PedidoDto>(
      `${this.clientesBaseUrl}/${pedidoId}/aceptar`,
      null,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Rechazar un pedido nuevo
   */
  rechazarPedido(pedidoId: string): Observable<PedidoDto> {
    return this.http.post<PedidoDto>(
      `${this.clientesBaseUrl}/${pedidoId}/rechazar`,
      null,
      { headers: this.getHeaders() }
    );
  }

  getRestaurantePorUsuario(usuarioId: string): Observable<any> {
    return this.http.get<any>(
      `${this.restaurantesBaseUrl}/usuario/${usuarioId}`,
      { headers: this.getHeaders() }
    );
  }
}
