import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment'; 

export interface Restaurante {
  id: string;
  nombre: string;
  descripcion: string;
  categoria: string;
  calificacionPromedio: number;
  imagenUrl: string;
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
}

export interface ResenaRestaurante {
  id: string;
  restauranteId: string;
  clienteId: string;
  pedidoId: string;
  calificacion: number;
  comentario: string;
  fechaCreacion: string;
}

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  // Endpoints para el microservicio de Restaurantes (puerto 8082)
  private restaurantesApiUrl = environment.restaurantesApi + '/api';
  
  // Endpoints para el microservicio de Cliente (puerto 8080)
  private clienteApiUrl = environment.clientesApi + '/api/pedidos'; 

  constructor(private http: HttpClient) {
    console.log('Base URL Restaurantes:', this.restaurantesApiUrl);
    console.log('Base URL Cliente:', this.clienteApiUrl);
  }

  getRestaurantes(): Observable<Restaurante[]> {
    return this.http.get<Restaurante[]>(`${this.restaurantesApiUrl}/restaurante`);
  }

  getRestauranteById(id: string): Observable<Restaurante> {
    return this.http.get<Restaurante>(`${this.restaurantesApiUrl}/restaurante/${id}`);
  }

  getProductosByRestaurante(restauranteId: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.restaurantesApiUrl}/productos/restaurante/${restauranteId}`);
  }

  // ⭐ Método para obtener reseñas consumiendo el endpoint del CLIENTE (puerto 8080)
  getResenasRestaurante(restauranteId: string): Observable<ResenaRestaurante[]> {
    return this.http.get<ResenaRestaurante[]>(`${this.clienteApiUrl}/restaurantes/${restauranteId}/reseñas`);
  }
}
