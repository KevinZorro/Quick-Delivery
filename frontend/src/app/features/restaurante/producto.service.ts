import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Producto {
  id?: string;
  nombre: string;
  descripcion: string;
  precio: number;
  disponible: boolean;
  imagenUrl?: string;
  restauranteId: string;
  categoria?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiUrl = 'http://localhost:8082/api/productos'; // Ajusta el puerto

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  listarPorRestaurante(restauranteId: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/restaurante/${restauranteId}`, {
      headers: this.getHeaders()
    });
  }

  crear(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.apiUrl, producto, {
      headers: this.getHeaders()
    });
  }

  actualizar(id: string, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/${id}`, producto, {
      headers: this.getHeaders()
    });
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  cambiarDisponibilidad(id: string, disponible: boolean): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/disponibilidad`, { disponible }, {
      headers: this.getHeaders()
    });
  }
}
