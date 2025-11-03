import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Restaurante {
  id: string;
  usuarioId: string;
  descripcion: string;
  categoria: string;
  calificacionPromedio: number;
  imagenUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private apiUrl = 'http://localhost:8081/api/restaurante';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  obtenerPorUsuarioId(usuarioId: string): Observable<Restaurante> {
    return this.http.get<Restaurante>(`${this.apiUrl}/usuario/${usuarioId}`, {
      headers: this.getHeaders()
    });
  }

  crear(restaurante: any): Observable<Restaurante> {
    return this.http.post<Restaurante>(this.apiUrl, restaurante, {
      headers: this.getHeaders()
    });
  }

  actualizar(id: string, restaurante: any): Observable<Restaurante> {
    return this.http.put<Restaurante>(`${this.apiUrl}/${id}`, restaurante, {
      headers: this.getHeaders()
    });
  }

  listarTodos(): Observable<Restaurante[]> {
    return this.http.get<Restaurante[]>(this.apiUrl, {
      headers: this.getHeaders()
    });
  }
}
