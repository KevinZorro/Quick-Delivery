import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';  // Ajusta la ruta según tu proyecto
import { isPlatformBrowser } from '@angular/common';

export interface ProductoRequest {
  usuarioId?: string; // Ya no se envía, se obtiene del token en el backend
  nombre: string;
  descripcion: string;
  precio: number;
  categoria: string;
  disponible: boolean;
  imagenUrl?: string;
}

export interface ProductoResponse {
  id: string;
  restauranteId: string;
  restauranteNombre: string;
  nombre: string;
  descripcion: string;
  precio: number;
  categoria: string;
  disponible: boolean;
  imagenUrl?: string;
  fechaCreacion: string;
  fechaActualizacion?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiUrl = environment.restaurantesApi + '/api/productos';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {
    console.log('Base URL para ProductoService:', this.apiUrl);
  }

  private getHeaders(): HttpHeaders {
    let token = '';
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('token') || '';
    }
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  obtenerMisProductos(): Observable<ProductoResponse[]> {
    return this.http.get<ProductoResponse[]>(
      `${this.apiUrl}/mis-productos`,
      { headers: this.getHeaders() }
    );
  }

  obtenerProductoPorId(id: string): Observable<ProductoResponse> {
    return this.http.get<ProductoResponse>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  crearProducto(producto: ProductoRequest): Observable<ProductoResponse> {
    return this.http.post<ProductoResponse>(
      this.apiUrl,
      producto,
      { headers: this.getHeaders() }
    );
  }

  actualizarProducto(id: string, producto: Partial<ProductoRequest>): Observable<ProductoResponse> {
    return this.http.put<ProductoResponse>(
      `${this.apiUrl}/${id}`,
      producto,
      { headers: this.getHeaders() }
    );
  }

  eliminarProducto(id: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  cambiarDisponibilidad(id: string, disponible: boolean): Observable<ProductoResponse> {
    return this.http.patch<ProductoResponse>(
      `${this.apiUrl}/${id}/disponibilidad?disponible=${disponible}`,
      {},
      { headers: this.getHeaders() }
    );
  }

  obtenerMisCategorias(): Observable<string[]> {
    return this.http.get<string[]>(
      `${this.apiUrl}/mis-categorias`,
      { headers: this.getHeaders() }
    );
  }
}
