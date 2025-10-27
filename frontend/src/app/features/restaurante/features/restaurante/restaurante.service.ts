import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Restaurante {
  nombre: string;
  direccion: string;
  telefono: string;
  correo: string;
  password: string;
  tipoCocina: string;
  documentosLegales: string; // Base64 del archivo
}

@Injectable({
  providedIn: 'root'
})
export class RestauranteService {
  private apiUrl = 'http://localhost:8081/api/restaurante';

  constructor(private http: HttpClient) {}

  registrarRestaurante(data: Restaurante): Observable<any> {
    // Construir payload exacto para RegisterRequest
    const payload = {
      nombre: data.nombre,
      direccion: data.direccion,
      telefono: data.telefono,
      correo: data.correo,
      password: data.password,
      tipoCocina: data.tipoCocina,
      documentosLegales: data.documentosLegales
    };

    return this.http.post(`${this.apiUrl}/registro-completo`, payload, {
      headers: { 'Content-Type': 'application/json' }
    });
  }
}
