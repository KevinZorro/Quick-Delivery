import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {

  private baseUrl = 'http://localhost:8082/api/delivery';

  constructor(private http: HttpClient) {}

  getHistorial(repartidorId: string, estado?: string): Observable<any[]> {
    let params: any = {};
    if (estado && estado !== 'TODOS') params.estado = estado;

    return this.http.get<any[]>(`${this.baseUrl}/${repartidorId}/historial`, {
      params
    });
  }

}
