import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Direccion {
  id?: string;
  calle: string;
  referencia?: string;
  ciudad: string;
  barrio: string;
  coordenadas?: string;
  usuario: string;
  tipoReferencia: 'CASA' | 'TRABAJO' | 'OTRO';
}

@Injectable({
  providedIn: 'root'
})
export class DireccionService {
  private apiUrl = 'http://localhost:8083/api/direcciones';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  // ⭐ NUEVO: Obtener MIS direcciones (usuario autenticado)
  obtenerMisDirecciones(): Observable<Direccion[]> {
    return this.http.get<Direccion[]>(`${this.apiUrl}/mis-direcciones`, {
      headers: this.getHeaders()
    });
  }

  getDireccionesByUsuario(usuarioId: string): Observable<Direccion[]> {
    return this.http.get<Direccion[]>(`${this.apiUrl}/usuario/${usuarioId}`, {
      headers: this.getHeaders()
    });
  }

  getDireccionById(id: string): Observable<Direccion> {
    return this.http.get<Direccion>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }

  crearDireccion(direccion: Direccion): Observable<Direccion> {
    return this.http.post<Direccion>(this.apiUrl, direccion, {
      headers: this.getHeaders()
    });
  }

  actualizarDireccion(id: string, direccion: Direccion): Observable<Direccion> {
    return this.http.put<Direccion>(`${this.apiUrl}/${id}`, direccion, {
      headers: this.getHeaders()
    });
  }

  eliminarDireccion(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }
}
