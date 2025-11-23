import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment'; // Ajusta ruta
import { PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

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
  private apiUrl = environment.edgeApi + '/api/direcciones';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {
    console.log('Base URL para DireccionRestauranteService:', this.apiUrl);
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
