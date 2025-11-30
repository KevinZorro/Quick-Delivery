import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { isPlatformBrowser } from '@angular/common';
import { PromocionRequest, PromocionResponse } from './promocion.types';

@Injectable({
  providedIn: 'root'
})
export class PromocionService {
  private apiUrl = environment.clientesApi + '/api/promociones';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {}

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

validarCodigo(codigo: string, clienteId: string, restauranteId: string): Observable<PromocionResponse> {
  return this.http.get<PromocionResponse>(
    `${this.apiUrl}/codigo/${codigo}?clienteId=${clienteId}&restauranteId=${restauranteId}`,
    { headers: this.getHeaders() }
  );
}




  getAllPromociones(): Observable<PromocionResponse[]> {
    return this.http.get<PromocionResponse[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  getMisPromociones(): Observable<PromocionResponse[]> {
  return this.http.get<PromocionResponse[]>(
    `${this.apiUrl}/restaurante`,
    { headers: this.getHeaders() }
  );
}

updatePromocion(id: string, data: {
  estado?: string;
  fechaExpiracion?: string;
  cantidadUsos?: number;
}): Observable<PromocionResponse> {
  return this.http.put<PromocionResponse>(
    `${this.apiUrl}/id/${id}`,
    data,
    { headers: this.getHeaders() }
  );
}



  getPromocionById(id: string): Observable<PromocionResponse> {
    return this.http.get<PromocionResponse>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  crearPromocion(promocion: PromocionRequest): Observable<PromocionResponse> {
    return this.http.post<PromocionResponse>(this.apiUrl, promocion, { headers: this.getHeaders() });
  }

  deletePromocion(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}
