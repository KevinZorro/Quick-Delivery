import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

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
  imports: [CommonModule],  // Esto importa ngIf, ngFor, pipes, etc.
  templateUrl: './product-catalog.component.html',
  styleUrls: ['./product-catalog.component.css']
})
export class ProductCatalogComponent implements OnInit {
  productos: Producto[] = [];
  isLoading = true;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadProductos();
  }

  loadProductos(): void {
    this.http.get<Producto[]>('http://localhost:8081/productos')
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
    console.log('Producto agregado:', producto);
    alert(`${producto.nombre} agregado al carrito`);
  }
}
