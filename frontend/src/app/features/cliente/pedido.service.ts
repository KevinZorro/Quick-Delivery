import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';  // Ajusta la ruta según tu proyecto
import { PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export interface ItemPedidoRequest {
  productoId: string;
  cantidad: number;
}

export interface CrearPedidoRequest {
  clienteId: string;
  restauranteId: string;
  metodoPago: string;
  direccionEntregaId?: string | null;
  preferencias?: string;
  items: ItemPedidoRequest[];
}

export interface ItemPedido {
  id: string;
  productoId: string;
  cantidad: number;
  precioUnidad: number;
  subtotal: number;
  nombreProducto?: string;
  descripcionProducto?: string;
  imagenProducto?: string;
}

export interface Pedido {
  id: string;
  clienteId: string;
  restauranteId: string;
  total: number;
  estado: string;
  metodoPago?: string;
  fechaCreacion: string;
  items?: ItemPedido[];
}

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private baseUrl = environment.clientesApi + '/api';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {
    console.log('Base URL para PedidoService:', this.baseUrl);
  }

  private getAuthHeaders(): HttpHeaders {
    let token = '';
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('quick-delivery-token') || '';
    }
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

  crearPedidoDesdeCarrito(request: CrearPedidoRequest): Observable<Pedido> {
    console.log('🚀 Servicio: Creando pedido desde carrito...');
    console.log('📦 Request completo:', request);
    const headers = this.getAuthHeaders();
    return this.http.post<Pedido>(
      `${this.baseUrl}/pedidos/crear-desde-carrito`,
      request,
      { headers }
    );
  }

  listarPedidosUsuario(usuarioId: string): Observable<Pedido[]> {
    const headers = this.getAuthHeaders();
    console.log('📦 Obteniendo pedidos del usuario:', usuarioId);
    return this.http.get<Pedido[]>(
      `${this.baseUrl}/pedidos/usuario/${usuarioId}`,
      { headers }
    );
  }

  listarPedidosUsuarioPorEstado(usuarioId: string, estado: string): Observable<Pedido[]> {
    const headers = this.getAuthHeaders();
    console.log('📦 Obteniendo pedidos del usuario:', usuarioId, 'con estado:', estado);
    return this.http.get<Pedido[]>(
      `${this.baseUrl}/pedidos/usuario/${usuarioId}/estado/${estado}`,
      { headers }
    );
  }

  contarPedidosUsuario(usuarioId: string): Observable<number> {
    const headers = this.getAuthHeaders();
    console.log('🔢 Contando pedidos del usuario:', usuarioId);
    return this.http.get<number>(
      `${this.baseUrl}/pedidos/usuario/${usuarioId}/count`,
      { headers }
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

  listarPedidosCliente(clienteId: string): Observable<Pedido[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Pedido[]>(
      `${this.baseUrl}/pedidos/cliente/${clienteId}`,
      { headers }
    );
  }
}
