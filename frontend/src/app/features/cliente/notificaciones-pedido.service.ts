import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface NotificacionEstadoPedido {
  pedidoId: string;
  clienteId: string;
  usuarioId: string;
  restauranteId: string;
  estado: string;
  titulo: string;
  mensaje: string;
  fecha: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificacionesPedidoService {
  private eventSource: EventSource | null = null;

  constructor(private zone: NgZone) {}

  conectar(usuarioId: string): Observable<NotificacionEstadoPedido> {
    return new Observable<NotificacionEstadoPedido>((observer) => {
      this.cerrar();

      const url = `${environment.clientesApi}/api/notificaciones/pedidos/stream?usuarioId=${usuarioId}`;
      this.eventSource = new EventSource(url);

      this.eventSource.addEventListener('estado-pedido', (event) => {
        const data = JSON.parse((event as MessageEvent).data) as NotificacionEstadoPedido;
        this.zone.run(() => observer.next(data));
      });

      this.eventSource.onerror = (error) => {
        this.zone.run(() => observer.error(error));
        this.cerrar();
      };

      return () => this.cerrar();
    });
  }

  cerrar(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }

  async pedirPermisoBrowser(): Promise<void> {
    if (!('Notification' in window) || Notification.permission !== 'default') {
      return;
    }

    await Notification.requestPermission();
  }

  mostrarBrowser(notificacion: NotificacionEstadoPedido): void {
    if (!('Notification' in window) || Notification.permission !== 'granted') {
      return;
    }

    new Notification(notificacion.titulo, {
      body: notificacion.mensaje,
      tag: notificacion.pedidoId
    });
  }
}
