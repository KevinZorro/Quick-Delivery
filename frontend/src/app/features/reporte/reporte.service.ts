import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteVentas } from './reporte.model';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private baseUrl = 'http://localhost:8081/reportes/ventas'; // ajusta según tu API

  constructor(private http: HttpClient) {}

  getReporteVentas(): Observable<ReporteVentas> {
    const userId = localStorage.getItem('quick-delivery-userId'); // opcional según tu API
    return this.http.get<ReporteVentas>(`${this.baseUrl}/${userId}`);
  }
}
