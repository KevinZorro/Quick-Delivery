// src/app/features/pedidos/pedidos.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

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

  private baseUrl = 'http://localhost:8081/api/restaurante';

  constructor(private http: HttpClient) {}

  getHistorial(restauranteId: string): Observable<PedidoDto[]> {
  return this.http.get<PedidoDto[]>(
    `${this.baseUrl}/${restauranteId}/historial-completo`
  );
}


  actualizarEstado(pedidoId: string, nuevoEstado: string) {
  return this.http.put(
    `${this.baseUrl}/pedidos/${pedidoId}/estado`,
    null,
    { params: { nuevoEstado } }
  );
}

/**
 * Aceptar un pedido nuevo
 */
aceptarPedido(pedidoId: string): Observable<PedidoDto> {
  return this.http.post<PedidoDto>(
    `http://localhost:8080/api/pedidos/${pedidoId}/aceptar`,
    null
  );
}

/**
 * Rechazar un pedido nuevo
 */
rechazarPedido(pedidoId: string): Observable<PedidoDto> {
  return this.http.post<PedidoDto>(
    `http://localhost:8080/api/pedidos/${pedidoId}/rechazar`,
    null
  );
}


getRestaurantePorUsuario(usuarioId: string): Observable<any> {
  return this.http.get<any>(`${this.baseUrl}/usuario/${usuarioId}`);
}


}
