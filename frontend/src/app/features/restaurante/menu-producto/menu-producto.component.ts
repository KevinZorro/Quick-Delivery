import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductoService, Producto, ProductoDTO } from '../shared/services/producto.service';

@Component({
  selector: 'app-menu-producto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './menu-producto.component.html',
  styleUrls: ['./menu-producto.component.css']
})
export class MenuProductoComponent implements OnInit {
  productos: Producto[] = [];
  productosFiltrados: Producto[] = [];
  restauranteId: string = '';
  loading = true;
  errorMessage: string | null = null;
  
  // Modal
  mostrarModal = false;
  modoEdicion = false;
  productoActual: Producto = this.nuevoProductoVacio();
  
  // Filtros
  busquedaNombre = '';
  categoriaFiltro = '';
  precioMin: number | null = null;
  precioMax: number | null = null;

  // Categorías disponibles (del backend)
  categorias = [
    'COMIDA_RAPIDA',
    'ITALIANA',
    'MEXICANA',
    'CHINA',
    'VEGETARIANA',
    'POSTRES',
    'BEBIDAS',
    'MARISCOS',
    'PARRILLA',
    'OTROS'
  ];

  constructor(
    private productoService: ProductoService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Obtener restauranteId de los query params
    this.route.queryParams.subscribe(params => {
      this.restauranteId = params['restauranteId'];
      if (this.restauranteId) {
        this.cargarProductos();
      } else {
        this.errorMessage = 'No se encontró el ID del restaurante';
        this.loading = false;
      }
    });
  }

  cargarProductos(): void {
    this.loading = true;
    this.productoService.listarPorRestaurante(this.restauranteId).subscribe({
      next: (productos) => {
        this.productos = productos;
        this.productosFiltrados = productos;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar los productos';
        this.loading = false;
        console.error(err);
      }
    });
  }

  // CRUD - Crear/Editar
  abrirModalCrear(): void {
    this.modoEdicion = false;
    this.productoActual = this.nuevoProductoVacio();
    this.mostrarModal = true;
  }

  abrirModalEditar(producto: Producto): void {
    this.modoEdicion = true;
    this.productoActual = { ...producto }; // Clonar el producto
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.productoActual = this.nuevoProductoVacio();
  }

  guardarProducto(): void {
    if (!this.validarProducto()) {
      alert('Por favor completa todos los campos obligatorios');
      return;
    }

    const productoDTO: ProductoDTO = {
      restauranteId: this.restauranteId,
      nombre: this.productoActual.nombre,
      descripcion: this.productoActual.descripcion,
      precio: this.productoActual.precio,
      categoria: this.productoActual.categoria,
      disponible: this.productoActual.disponible,
      imagenUrl: this.productoActual.imagenUrl
    };

    if (this.modoEdicion && this.productoActual.id) {
      // Actualizar
      this.productoService.actualizar(this.productoActual.id, productoDTO).subscribe({
        next: () => {
          alert('Producto actualizado exitosamente');
          this.cargarProductos();
          this.cerrarModal();
        },
        error: (err) => {
          alert('Error al actualizar el producto');
          console.error(err);
        }
      });
    } else {
      // Crear
      this.productoService.crear(productoDTO).subscribe({
        next: () => {
          alert('Producto creado exitosamente');
          this.cargarProductos();
          this.cerrarModal();
        },
        error: (err) => {
          alert('Error al crear el producto');
          console.error(err);
        }
      });
    }
  }

  eliminarProducto(id: string): void {
    if (confirm('¿Estás seguro de que deseas eliminar este producto?')) {
      this.productoService.eliminar(id).subscribe({
        next: () => {
          alert('Producto eliminado exitosamente');
          this.cargarProductos();
        },
        error: (err) => {
          alert('Error al eliminar el producto');
          console.error(err);
        }
      });
    }
  }

  // Filtros
  aplicarFiltros(): void {
    let resultados = [...this.productos];

    // Filtro por nombre
    if (this.busquedaNombre) {
      resultados = resultados.filter(p => 
        p.nombre.toLowerCase().includes(this.busquedaNombre.toLowerCase())
      );
    }

    // Filtro por categoría
    if (this.categoriaFiltro) {
      resultados = resultados.filter(p => p.categoria === this.categoriaFiltro);
    }

    // Filtro por precio
    if (this.precioMin !== null) {
      resultados = resultados.filter(p => p.precio >= this.precioMin!);
    }
    if (this.precioMax !== null) {
      resultados = resultados.filter(p => p.precio <= this.precioMax!);
    }

    this.productosFiltrados = resultados;
  }

  limpiarFiltros(): void {
    this.busquedaNombre = '';
    this.categoriaFiltro = '';
    this.precioMin = null;
    this.precioMax = null;
    this.productosFiltrados = [...this.productos];
  }

  // Helpers
  nuevoProductoVacio(): Producto {
    return {
      nombre: '',
      descripcion: '',
      precio: 0,
      categoria: '',
      disponible: true,
      imagenUrl: 'https://via.placeholder.com/200'
    };
  }

  validarProducto(): boolean {
    return !!(
      this.productoActual.nombre &&
      this.productoActual.precio > 0 &&
      this.productoActual.categoria
    );
  }

  volverInicio(): void {
    this.router.navigate(['/inicio']);
  }
}
