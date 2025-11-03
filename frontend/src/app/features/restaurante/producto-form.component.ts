import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProductoService, Producto } from './producto.service';

@Component({
  selector: 'app-producto-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './producto-form.component.html'
})
export class ProductoFormComponent implements OnInit {
  @Input() restauranteId!: string;
  @Input() producto: Producto | null = null;
  @Output() cerrar = new EventEmitter<void>();
  @Output() guardado = new EventEmitter<void>();

  productoForm!: FormGroup;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private productoService: ProductoService
  ) {}

  ngOnInit(): void {
    this.productoForm = this.fb.group({
      nombre: [this.producto?.nombre || '', Validators.required],
      descripcion: [this.producto?.descripcion || '', Validators.required],
      precio: [this.producto?.precio || 0, [Validators.required, Validators.min(0)]],
      disponible: [this.producto?.disponible ?? true],
      imagenUrl: [this.producto?.imagenUrl || ''],
      categoria: [this.producto?.categoria || '']
    });
  }

  onSubmit(): void {
    if (this.productoForm.invalid) {
      return;
    }

    this.loading = true;
    const productoData: Producto = {
      ...this.productoForm.value,
      restauranteId: this.restauranteId
    };

    const operation = this.producto && this.producto.id
      ? this.productoService.actualizar(this.producto.id, productoData)
      : this.productoService.crear(productoData);

    operation.subscribe({
      next: () => {
        this.loading = false;
        this.guardado.emit();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error al guardar el producto';
        console.error(err);
      }
    });
  }

  cancelar(): void {
    this.cerrar.emit();
  }
}
