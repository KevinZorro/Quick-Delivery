import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment'; // Ajusta la ruta según tu proyecto

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

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private baseUrl = environment.restaurantesApi + '/api';

  constructor(private http: HttpClient) {
    console.log('Base URL para RestauranteService:', this.baseUrl);
  }

  getRestaurantes(): Observable<Restaurante[]> {
    return this.http.get<Restaurante[]>(`${this.baseUrl}/restaurante`);
  }

  getRestauranteById(id: string): Observable<Restaurante> {
    return this.http.get<Restaurante>(`${this.baseUrl}/restaurante/${id}`);
  }

  getProductosByRestaurante(restauranteId: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.baseUrl}/productos/restaurante/${restauranteId}`);
  }
}
