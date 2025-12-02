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

usuarioId = localStorage.getItem('quick-delivery-userId') ?? '';
restauranteId = '';
  constructor(private pedidosService: PedidosService) {}

  ngOnInit(): void {
    this.obtenerRestauranteDelUsuario();
  }

  obtenerRestauranteDelUsuario() {
    this.pedidosService.getRestaurantePorUsuario(this.usuarioId).subscribe({
      next: (restaurante) => {
        this.restauranteId = restaurante.id;
        this.cargarHistorial();
      },
      error: (err) => {
        console.error("Error obteniendo restaurante del usuario", err);
        this.cargando = false;
        this.mensaje = "No se pudo cargar tu restaurante ❌";
      }
    });
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
