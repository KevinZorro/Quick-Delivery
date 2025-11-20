// src/app/features/pedidos/pedidos.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PedidoDto {
  id: string;
  restauranteId: string;
  total: number;
  fechaCreacion: string;
  estado: string;
  items: any[] | null;
}

@Injectable({ providedIn: 'root' })
export class PedidosService {

  private baseUrl = 'http://localhost:8081/api/restaurante';

  constructor(private http: HttpClient) {}

  getHistorial(restauranteId: string): Observable<PedidoDto[]> {
  return this.http.get<PedidoDto[]>(
    `${this.baseUrl}/${restauranteId}/historial-completo`
  );
}


  actualizarEstado(pedidoId: string, nuevoEstado: string) {
  return this.http.put(
    `${this.baseUrl}/pedidos/${pedidoId}/estado`,
    null,
    { params: { nuevoEstado } }
  );
}

}
