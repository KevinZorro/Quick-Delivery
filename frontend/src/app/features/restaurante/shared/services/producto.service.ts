import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Producto {
  id?: string;
  restauranteId?: string;
  nombre: string;
  descripcion: string;
  precio: number;
  categoria: string;
  disponible: boolean;
  imagenUrl: string;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface ProductoDTO {
  restauranteId: string;
  nombre: string;
  descripcion: string;
  precio: number;
  categoria: string;
  disponible: boolean;
  imagenUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private baseUrl = 'http://localhost:8081/api/productos';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.baseUrl);
  }

  listarPorRestaurante(restauranteId: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.baseUrl}/restaurante/${restauranteId}`);
  }

  obtenerPorId(id: string): Observable<Producto> {
    return this.http.get<Producto>(`${this.baseUrl}/${id}`);
  }

  crear(producto: ProductoDTO): Observable<Producto> {
    return this.http.post<Producto>(this.baseUrl, producto);
  }

  actualizar(id: string, producto: ProductoDTO): Observable<Producto> {
    return this.http.put<Producto>(`${this.baseUrl}/${id}`, producto);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  buscarPorNombre(restauranteId: string, nombre: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.baseUrl}/restaurante/${restauranteId}/buscar?nombre=${nombre}`);
  }

  filtrarPorPrecio(restauranteId: string, min: number, max: number): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.baseUrl}/restaurante/${restauranteId}/precio?min=${min}&max=${max}`);
  }

  filtrarPorCategoria(restauranteId: string, categoria: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.baseUrl}/restaurante/${restauranteId}/categoria/${categoria}`);
  }
}
