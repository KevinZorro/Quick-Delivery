import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';

import { HorarioAtencion, HorarioAtencionDto } from './horarios.types';

@Injectable({
  providedIn: 'root'
})
export class HorarioService {

  private apiUrl = environment.restaurantesApi + '/api/restaurante';

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

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

  // =============== CRUD HORARIOS ==================

  getHorarios(restauranteId: string): Observable<HorarioAtencion[]> {
    return this.http.get<HorarioAtencion[]>(
      `${this.apiUrl}/${restauranteId}/horarios`,
      { headers: this.getHeaders() }
    );
  }

  crearHorario(restauranteId: string, dto: HorarioAtencionDto): Observable<HorarioAtencion> {
    return this.http.post<HorarioAtencion>(
      `${this.apiUrl}/${restauranteId}/horarios`,
      dto,
      { headers: this.getHeaders() }
    );
  }

  actualizarHorario(restauranteId: string, horarioId: string, dto: HorarioAtencionDto): Observable<HorarioAtencion> {
    return this.http.put<HorarioAtencion>(
      `${this.apiUrl}/${restauranteId}/horarios/${horarioId}`,
      dto,
      { headers: this.getHeaders() }
    );
  }

  eliminarHorario(restauranteId: string, horarioId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${restauranteId}/horarios/${horarioId}`,
      { headers: this.getHeaders() }
    );
  }


  // ============ INTERRUPCIONES ESPECIALES ============

  listarInterrupciones(restauranteId: string) {
    return this.http.get(
      `${this.apiUrl}/${restauranteId}/horarios/interrupciones`,
      { headers: this.getHeaders() }
    );
  }

  crearInterrupcion(restauranteId: string, data: any) {
    return this.http.post(
      `${this.apiUrl}/${restauranteId}/horarios/interrupciones`,
      data,
      { headers: this.getHeaders() }
    );
  }

  eliminarInterrupcion(restauranteId: string, interrupcionId: string) {
    return this.http.delete(
      `${this.apiUrl}/${restauranteId}/horarios/interrupciones/${interrupcionId}`,
      { headers: this.getHeaders() }
    );
  }

}
