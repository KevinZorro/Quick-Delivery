import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { PedidoComponent } from '../pedido/pedido.component';
import { environment } from '../../../../environments/environment';

interface Producto {
  id: string;
  nombre: string;
  descripcion: string;
  precio: number;
  imagenUrl: string;
  disponible: boolean;
}

@Component({
  selector: 'app-product-catalog',
  standalone: true,
  imports: [CommonModule, PedidoComponent],
  templateUrl: './product-catalog.component.html',
  styleUrls: ['./product-catalog.component.css']
})
export class ProductCatalogComponent implements OnInit {
  productos: Producto[] = [];
  isLoading = true;
  productoSeleccionado: Producto | null = null;
  mostrarModalPedido = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadProductos();
  }

  loadProductos(): void {
    console.error(`${environment.restaurantesApi}`)
    this.http.get<Producto[]>(`${environment.restaurantesApi}/productos`)
      .subscribe({
        next: (data) => {
          this.productos = data.filter(p => p.disponible);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error al cargar productos:', error);
          this.isLoading = false;
        }
      });
  }

  agregarAlCarrito(producto: Producto): void {
    this.productoSeleccionado = producto;
    this.mostrarModalPedido = true;
  }

  cerrarModalPedido(): void {
    this.mostrarModalPedido = false;
    this.productoSeleccionado = null;
  }
}
