import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environment';

interface Producto {
  id: string;
  nombre: string;
  precio: number;
}

@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pedido.component.html',
  styleUrls: ['./pedido.component.css']
})
export class PedidoComponent {
  @Input() producto: Producto | null = null;
  @Input() clienteId: string = '';
  @Output() cerrarModal = new EventEmitter<void>();

  pedidoForm: FormGroup;
  isSubmitting = false;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.pedidoForm = this.fb.group({
      cantidad: [1, [Validators.required, Validators.min(1)]],
      metodoPago: ['EFECTIVO', Validators.required],
      instrucciones: ['']
    });
  }

  get subtotal(): number {
    if (!this.producto) return 0;
    return this.producto.precio * (this.pedidoForm.get('cantidad')?.value || 1);
  }

  enviarPedido(): void {
    if (this.pedidoForm.invalid || !this.producto) return;

    this.isSubmitting = true;

    const pedido = {
      clienteId: this.clienteId,
      productoId: this.producto.id,
      cantidad: this.pedidoForm.value.cantidad,
      precioUnidad: this.producto.precio,
      subtotal: this.subtotal,
      metodoPago: this.pedidoForm.value.metodoPago,
      instrucciones: this.pedidoForm.value.instrucciones,
      fechaPedido: new Date(),
      estado: 'CREADO'
    };

    // 👇 Usa la URL dinámica desde environment
    this.http.post(`${environment.clientesApi}/api/pedidos`, pedido)
      .subscribe({
        next: (response) => {
          console.log('Pedido creado:', response);
          alert('✅ Pedido realizado con éxito');
          this.cerrar();
        },
        error: (error) => {
          console.error('❌ Error al crear pedido:', error);
          alert('Error al crear el pedido');
          this.isSubmitting = false;
        }
      });
  }

  cerrar(): void {
    this.cerrarModal.emit();
  }
}
