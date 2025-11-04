import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteVentas } from './reporte.model';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private baseUrl = 'http://localhost:8081/reportes/ventas';

  constructor(private http: HttpClient) {}

  getReporteVentas(): Observable<ReporteVentas> {
    const userId = localStorage.getItem('quick-delivery-userId');
    return this.http.get<ReporteVentas>(`${this.baseUrl}/${userId}`);
  }

  downloadExcel(): Observable<Blob> {
    const userId = localStorage.getItem('quick-delivery-userId');
    return this.http.get(`${this.baseUrl}/${userId}/excel`, {
      responseType: 'blob'
    });
  }
}
