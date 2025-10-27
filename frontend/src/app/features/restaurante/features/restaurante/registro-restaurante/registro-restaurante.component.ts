import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestauranteService, Restaurante } from '../restaurante.service';

@Component({
  selector: 'app-registro-restaurante',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './registro-restaurante.component.html',
  styleUrls: ['./registro-restaurante.component.css']
})
export class RegistroRestauranteComponent {
  restaurante: Restaurante = {
    nombre: '',
    direccion: '',
    telefono: '',
    correo: '',
    password: '',
    tipoCocina: '',
    documentosLegales: ''
  };

  mensaje: string = '';

  constructor(private restauranteService: RestauranteService) {}

  // ✅ Método para registrar el restaurante
  registrar() {
    // Validar campos obligatorios antes de enviar
    if (!this.restaurante.nombre || !this.restaurante.correo || !this.restaurante.password) {
      this.mensaje = '❌ Debes completar los campos obligatorios';
      return;
    }

    // Enviar al backend
    this.restauranteService.registrarRestaurante(this.restaurante).subscribe({
      next: (res) => this.mensaje = '✅ Registro exitoso: ' + res.message,
      error: (err) => this.mensaje = '❌ Error: ' + (err.error?.message || 'No se pudo registrar')
    });
  }

  // ✅ Manejo de archivo legal como Base64
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.restaurante.documentosLegales = reader.result as string;
      };
      reader.readAsDataURL(file); // convierte el archivo a Base64
    }
  }
}
