// pedido.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ItemPedidoRequest {
  productoId: string;
  cantidad: number;
}

export interface CrearPedidoRequest {
  clienteId: string;           // ⭐ AGREGAR
  restauranteId: string;
  metodoPago: string;           // ⭐ AGREGAR
  direccionEntregaId?: string | null;
  preferencias?: string;
  items: ItemPedidoRequest[];
}

export interface Pedido {
  id: string;
  clienteId: string;
  restauranteId: string;
  total: number;
  estado: string;
  metodoPago?: string;
  fechaCreacion: string;
}

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ⭐ Método helper para obtener headers con token
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('quick-delivery-token');
    console.log('🔑 Obteniendo token:', token ? 'SÍ existe' : 'NO existe');
    
    if (token) {
      console.log('📄 Token (primeros 50 chars):', token.substring(0, 50) + '...');
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
    }
    
    console.warn('⚠️ NO hay token - petición sin autenticación');
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  // ⭐ Crear pedido con token manual
  crearPedidoDesdeCarrito(request: CrearPedidoRequest): Observable<Pedido> {
    console.log('🚀 Servicio: Creando pedido desde carrito...');
    console.log('📦 Request completo:', request);
    
    const headers = this.getAuthHeaders();
    
    return this.http.post<Pedido>(
      `${this.baseUrl}/pedidos/crear-desde-carrito`,
      request,
      { headers } // ⭐ Enviar headers con token
    );
  }

  actualizarEstado(pedidoId: string, estado: string): Observable<Pedido> {
    const headers = this.getAuthHeaders();
    return this.http.patch<Pedido>(
      `${this.baseUrl}/pedidos/${pedidoId}/estado`,
      null,
      { headers, params: { estado } }
    );
  }

  actualizarMetodoPago(pedidoId: string, metodoPago: string): Observable<Pedido> {
    const headers = this.getAuthHeaders();
    return this.http.patch<Pedido>(
      `${this.baseUrl}/pedidos/${pedidoId}/metodopago`,
      null,
      { headers, params: { metodoPago } }
    );
  }

  obtenerPedido(pedidoId: string): Observable<Pedido> {
    const headers = this.getAuthHeaders();
    return this.http.get<Pedido>(`${this.baseUrl}/pedidos/${pedidoId}`, { headers });
  }

  listarPedidos(): Observable<Pedido[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Pedido[]>(`${this.baseUrl}/pedidos`, { headers });
  }
}
