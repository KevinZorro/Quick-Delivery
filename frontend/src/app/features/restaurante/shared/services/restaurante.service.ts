import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Restaurante {
  id: string;
  usuarioId: string;
  descripcion: string;
  categoria: string;
  calificacionPromedio: number;
  imagenUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private baseUrl = 'http://localhost:8081/api/restaurante';

  constructor(private http: HttpClient) {}

  obtenerPorId(id: string): Observable<Restaurante> {
    return this.http.get<Restaurante>(`${this.baseUrl}/${id}`);
  }

  listarPorUsuarioId(usuarioId: string): Observable<Restaurante[]> {
    return this.http.get<Restaurante[]>(`${this.baseUrl}/usuario/${usuarioId}`);
  }

  crear(restaurante: Partial<Restaurante>): Observable<Restaurante> {
    return this.http.post<Restaurante>(this.baseUrl, restaurante);
  }

  actualizar(id: string, restaurante: Partial<Restaurante>): Observable<Restaurante> {
    return this.http.put<Restaurante>(`${this.baseUrl}/${id}`, restaurante);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  actualizarCalificacion(id: string, nuevaCalificacion: number): Observable<Restaurante> {
    return this.http.put<Restaurante>(`${this.baseUrl}/${id}/calificacion?nuevaCalificacion=${nuevaCalificacion}`, {});
  }
}
