import { Injectable } from '@angular/core';
import { Producto } from './restaurante.service';

export interface CarritoItem {
  producto: Producto;
  cantidad: number;
}

@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  private carritoKey = 'quick-delivery-carrito';

  constructor() {}

  obtenerCarrito(): CarritoItem[] {
    const json = sessionStorage.getItem(this.carritoKey);
    return json ? JSON.parse(json) : [];
  }

  guardarCarrito(items: CarritoItem[]): void {
    sessionStorage.setItem(this.carritoKey, JSON.stringify(items));
  }

  agregarProducto(producto: Producto, cantidad: number = 1, preferencias: string = ''): void {
    const carrito = this.obtenerCarrito();
    const index = carrito.findIndex(item => item.producto.id === producto.id);

    if (index !== -1) {
      // Sumar cantidad si ya existe
      carrito[index].cantidad += cantidad;
    } else {
      carrito.push({ producto, cantidad });
    }

    this.guardarCarrito(carrito);
  }

// carrito.service.ts

incrementarCantidad(productoId: string) {
  const carrito = this.obtenerCarrito();
  const item = carrito.find(i => i.producto.id === productoId);
  if (item) {
    item.cantidad++;
    this.guardarCarrito(carrito);
  }
}

disminuirCantidad(productoId: string) {
  const carrito = this.obtenerCarrito();
  const index = carrito.findIndex(i => i.producto.id === productoId);
  if (index !== -1) {
    carrito[index].cantidad--;
    if (carrito[index].cantidad <= 0) {
      carrito.splice(index, 1);
    }
    this.guardarCarrito(carrito);
  }
}

obtenerTotal(): number {
  return this.obtenerCarrito()
    .reduce((acc, item) => acc + (item.producto.precio * item.cantidad), 0);
}




}
