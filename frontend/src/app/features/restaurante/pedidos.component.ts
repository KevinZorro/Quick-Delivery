// src/app/features/pedidos/pedidos.component.ts
import { Component, OnInit } from '@angular/core';
import { PedidosService, PedidoDto } from './pedidos.service';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pedidos.component.html'
})

export class PedidosComponent implements OnInit {

  pedidos: PedidoDto[] = [];
  cargando = true;

  restauranteId = "1e7290a8-d5b5-4d41-8f44-d225973ad883";  // ← cámbialo por el real

  constructor(private pedidosService: PedidosService) {}

  ngOnInit(): void {
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
}
