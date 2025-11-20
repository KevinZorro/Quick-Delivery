// src/app/features/pedidos/pedidos.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PedidosService, PedidoDto } from './pedidos.service';


@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedidos.component.html'
})
export class PedidosComponent implements OnInit {

  pedidos: PedidoDto[] = [];
  cargando = true;
  mensaje: string = '';
  cargandoCambio: string | null = null;

  estados = [
    'INICIADO',
    'EN_COCINA',
    'CON_EL_REPARTIDOR',
    'ENTREGADO'
  ];

  restauranteId = "1e7290a8-d5b5-4d41-8f44-d225973ad883"; 

  constructor(private pedidosService: PedidosService) {}

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial() {
    this.cargando = true;
    this.pedidosService.getHistorial(this.restauranteId).subscribe({
      next: data => {
        this.pedidos = data;
        this.cargando = false;
      },
      error: err => {
        console.error(err);
        this.cargando = false;
      }
    });
  }

  cambiarEstado(pedidoId: string, nuevoEstado: string) {
    this.cargandoCambio = pedidoId;

    this.pedidosService.actualizarEstado(pedidoId, nuevoEstado).subscribe({
      next: () => {
        this.mensaje = "Estado actualizado ✔️";
        this.cargandoCambio = null;
        this.cargarHistorial();
        setTimeout(() => this.mensaje = '', 2000);
      },
      error: err => {
        console.error(err);
        this.mensaje = "Error al actualizar ❌";
        this.cargandoCambio = null;
      }
    });
  }
}
