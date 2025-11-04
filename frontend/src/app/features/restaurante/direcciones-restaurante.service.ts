import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DireccionRestaurante {
  id?: string;
  calle: string;
  referencia?: string;
  ciudad: string;
  barrio: string;
  coordenadas?: string;
  usuario: string;
  tipoReferencia: 'SEDE_PRINCIPAL' | 'SUCURSAL' | 'COCINA_CENTRAL';
}

@Injectable({
  providedIn: 'root'
})
export class DireccionRestauranteService {
  private apiUrl = 'http://localhost:8083/api/direcciones';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // ⭐ NUEVO: Obtener MIS direcciones (restaurante autenticado)
  obtenerMisDirecciones(): Observable<DireccionRestaurante[]> {
    return this.http.get<DireccionRestaurante[]>(`${this.apiUrl}/mis-direcciones`, {
      headers: this.getHeaders()
    });
  }

  getDireccionesByUsuario(usuarioId: string): Observable<DireccionRestaurante[]> {
    return this.http.get<DireccionRestaurante[]>(`${this.apiUrl}/usuario/${usuarioId}`, {
      headers: this.getHeaders()
    });
  }

  getDireccionById(id: string): Observable<DireccionRestaurante> {
    return this.http.get<DireccionRestaurante>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  crearDireccion(direccion: DireccionRestaurante): Observable<DireccionRestaurante> {
    return this.http.post<DireccionRestaurante>(this.apiUrl, direccion, {
      headers: this.getHeaders()
    });
  }

  actualizarDireccion(id: string, direccion: DireccionRestaurante): Observable<DireccionRestaurante> {
    return this.http.put<DireccionRestaurante>(`${this.apiUrl}/${id}`, direccion, {
      headers: this.getHeaders()
    });
  }

  eliminarDireccion(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }
}
