import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteVentas } from './reporte.model';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';  // Ajusta ruta si es necesario

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private baseUrl = environment.restaurantesApi + '/reportes/ventas';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {
    console.log('Base URL para ReporteService:', this.baseUrl);
  }

  getReporteVentas(): Observable<ReporteVentas> {
    let userId = null;
    if (isPlatformBrowser(this.platformId)) {
      userId = localStorage.getItem('quick-delivery-userId');
    }
    if (!userId) {
      throw new Error('No se pudo obtener userId desde localStorage');
    }
    return this.http.get<ReporteVentas>(`${this.baseUrl}/${userId}`);
  }

  downloadExcel(): Observable<Blob> {
    let userId = null;
    if (isPlatformBrowser(this.platformId)) {
      userId = localStorage.getItem('quick-delivery-userId');
    }
    if (!userId) {
      throw new Error('No se pudo obtener userId desde localStorage');
    }
    return this.http.get(`${this.baseUrl}/${userId}/excel`, {
      responseType: 'blob'
    });
  }
}
