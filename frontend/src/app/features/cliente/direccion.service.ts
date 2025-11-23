import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';  // Ajusta ruta

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
  private apiUrl = environment.edgeApi + '/api/direcciones';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {
    console.log('Base URL para DireccionService:', this.apiUrl);
  }

  private getHeaders(): HttpHeaders {
    let token = '';
    if (isPlatformBrowser(this.platformId)) {
      token = localStorage.getItem('quick-delivery-token') || '';
    }
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

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
