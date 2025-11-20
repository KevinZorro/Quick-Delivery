import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductoService, ProductoResponse, ProductoRequest } from './producto.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-restaurante-main',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './main.component.html'
})
export class RestauranteMainComponent implements OnInit {
  productos: ProductoResponse[] = [];
  productosFiltrados: ProductoResponse[] = [];
  categorias: string[] = [];
  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  private platformId = inject(PLATFORM_ID);

  // Modal
  mostrarModal = false;
  modoEdicion = false;
  productoForm!: FormGroup;
  productoSeleccionado: ProductoResponse | null = null;


  // Filtros
  categoriaFiltro = '';
  busqueda = '';


  // Datos del restaurante
  restauranteId: string = '';
  restauranteNombre: string = '';


  constructor(
    private productoService: ProductoService,
    private fb: FormBuilder,
    private router: Router
  ) {}


  ngOnInit(): void {
    // ⭐ Ya no necesitas restauranteId, solo el nombre - SOLO en el navegador
    if (isPlatformBrowser(this.platformId)) {
      this.restauranteNombre = localStorage.getItem('quick-delivery-userName') || '';

      if (!localStorage.getItem('token')) {
        this.router.navigate(['/login']);
        return;
      }

      this.cargarProductos();
      this.cargarCategorias();
    }

    this.inicializarFormulario();
  }


  cargarProductos(): void {
    this.loading = true;
    this.errorMessage = null;

    // ⭐ Usar el nuevo endpoint que obtiene productos del usuario autenticado
    this.productoService.obtenerMisProductos().subscribe({
      next: (productos) => {
        this.productos = productos;
        this.aplicarFiltros();
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar los productos';
        console.error(err);
        this.loading = false;
      }
    });
  }

  cargarCategorias(): void {
    // ⭐ Usar el nuevo endpoint
    this.productoService.obtenerMisCategorias().subscribe({
      next: (categorias) => {
        this.categorias = categorias;
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
      }
    });
  }

  guardarProducto(): void {
    if (this.productoForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const formValue = this.productoForm.value;

    if (this.modoEdicion && this.productoSeleccionado) {
      // Actualizar producto existente
      this.productoService.actualizarProducto(this.productoSeleccionado.id, formValue).subscribe({
        next: () => {
          this.successMessage = 'Producto actualizado exitosamente';
          this.cerrarModal();
          this.cargarProductos();
          this.cargarCategorias();
          this.loading = false;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (err) => {
          this.errorMessage = 'Error al actualizar el producto';
          console.error(err);
          this.loading = false;
        }
      });
    } else {
      // ⭐ Crear nuevo producto - Ya no necesitas enviar usuarioId
      this.productoService.crearProducto(formValue).subscribe({
        next: () => {
          this.successMessage = 'Producto creado exitosamente';
          this.cerrarModal();
          this.cargarProductos();
          this.cargarCategorias();
          this.loading = false;
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (err) => {
          this.errorMessage = 'Error al crear el producto';
          console.error(err);
          this.loading = false;
        }
      });
    }
  }

  inicializarFormulario(): void {
    this.productoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(120)]],
      descripcion: ['', [Validators.maxLength(1000)]],
      precio: ['', [Validators.required, Validators.min(0.01)]],
      categoria: ['', [Validators.maxLength(80)]],
      disponible: [true, Validators.required],
      imagenUrl: ['', [Validators.maxLength(500)]]
    });
  }

  aplicarFiltros(): void {
    this.productosFiltrados = this.productos.filter(producto => {
      const cumpleCategoria = !this.categoriaFiltro || producto.categoria === this.categoriaFiltro;
      const cumpleBusqueda = !this.busqueda || 
        producto.nombre.toLowerCase().includes(this.busqueda.toLowerCase());
      return cumpleCategoria && cumpleBusqueda;
    });
  }

  onCategorialFiltroChange(event: Event): void {
    this.categoriaFiltro = (event.target as HTMLSelectElement).value;
    this.aplicarFiltros();
  }

  onBusquedaChange(event: Event): void {
    this.busqueda = (event.target as HTMLInputElement).value;
    this.aplicarFiltros();
  }

  abrirModalCrear(): void {
    this.modoEdicion = false;
    this.productoSeleccionado = null;
    this.productoForm.reset({ disponible: true });
    this.mostrarModal = true;
  }

  abrirModalEditar(producto: ProductoResponse): void {
    this.modoEdicion = true;
    this.productoSeleccionado = producto;
    this.productoForm.patchValue({
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      precio: producto.precio,
      categoria: producto.categoria,
      disponible: producto.disponible,
      imagenUrl: producto.imagenUrl
    });
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.productoForm.reset();
    this.productoSeleccionado = null;
  }

  eliminarProducto(producto: ProductoResponse): void {
    if (!confirm(`¿Estás seguro de eliminar "${producto.nombre}"?`)) {
      return;
    }

    this.loading = true;
    this.productoService.eliminarProducto(producto.id).subscribe({
      next: () => {
        this.successMessage = 'Producto eliminado exitosamente';
        this.cargarProductos();
        this.cargarCategorias();
        this.loading = false;
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (err) => {
        this.errorMessage = 'Error al eliminar el producto';
        console.error(err);
        this.loading = false;
      }
    });
  }

  toggleDisponibilidad(producto: ProductoResponse): void {
    this.productoService.cambiarDisponibilidad(producto.id, !producto.disponible).subscribe({
      next: () => {
        this.successMessage = `Producto ${!producto.disponible ? 'disponible' : 'no disponible'}`;
        this.cargarProductos();
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (err) => {
        this.errorMessage = 'Error al cambiar disponibilidad';
        console.error(err);
      }
    });
  }

  // ⭐ NUEVO: Método para navegar a las direcciones del restaurante
  verDirecciones(): void {
    this.router.navigate(['/restaurante/direcciones']);
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('quick-delivery-userId');
    localStorage.removeItem('quick-delivery-userName');
    localStorage.removeItem('quick-delivery-userRole');
    this.router.navigate(['/login']);
  }

  // ✅ NUEVO: Navegar a reportes
verReportes(): void {
  this.router.navigate(['/dashboard-reportes']);
}


verHistorialPedidos(): void {
  this.router.navigate(['/restaurante/pedidos']);
}

}
