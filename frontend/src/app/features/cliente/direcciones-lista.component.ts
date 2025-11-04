import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { DireccionService, Direccion } from './direccion.service';
import { HeaderComponent } from './header.component';

declare var google: any; // Google Maps

@Component({
  selector: 'app-direcciones-lista',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './direcciones-lista.component.html'
})
export class DireccionesListaComponent implements OnInit {
  direcciones: Direccion[] = [];
  loading = true;
  errorMessage: string | null = null;
  usuarioId: string = '';

  // ⭐ Variables para el modal del mapa
  modalMapaAbierto = false;
  direccionSeleccionada: Direccion | null = null;
  map: any;
  marker: any;

  constructor(
    private direccionService: DireccionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // ⭐ Obtener el userId del localStorage
    const clienteId = localStorage.getItem('quick-delivery-userId');
    
    if (!clienteId) {
      this.errorMessage = 'Error: No se pudo identificar el usuario. Por favor inicia sesión nuevamente.';
      this.loading = false;
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
      return;
    }

    this.usuarioId = clienteId;
    this.loadDirecciones();
  }

  loadDirecciones(): void {
    this.direccionService.getDireccionesByUsuario(this.usuarioId).subscribe({
      next: (data) => {
        this.direcciones = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar direcciones';
        this.loading = false;
        console.error(err);
      }
    });
  }

  editarDireccion(id: string): void {
    this.router.navigate(['/cliente/direcciones/editar', id]);
  }

  eliminarDireccion(id: string): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta dirección?')) {
      this.direccionService.eliminarDireccion(id).subscribe({
        next: () => {
          this.loadDirecciones();
        },
        error: (err) => {
          alert('Error al eliminar la dirección');
          console.error(err);
        }
      });
    }
  }

  agregarDireccion(): void {
    // ⭐ Validar límite de 5 direcciones
    if (this.direcciones.length >= 5) {
      alert('Has alcanzado el límite máximo de 5 direcciones. Elimina una dirección existente antes de agregar una nueva.');
      return;
    }
    this.router.navigate(['/cliente/direcciones/nueva']);
  }

  // ⭐ NUEVO: Abrir modal con mapa
  abrirModalMapa(direccion: Direccion): void {
    if (!direccion.coordenadas || direccion.coordenadas.trim() === '') {
      alert('Esta dirección no tiene coordenadas');
      return;
    }

    this.direccionSeleccionada = direccion;
    this.modalMapaAbierto = true;

// Dentro de abrirModalMapa(direccion: Direccion)
setTimeout(() => {
  const mapElement = document.getElementById('modalMapCliente');
  if (mapElement) {
    this.initMap();
  } else {
    alert('Error al cargar el mapa. Intenta de nuevo.');
  }
}, 200);
  }

  // ⭐ NUEVO: Cerrar modal
  cerrarModalMapa(): void {
    this.modalMapaAbierto = false;
    this.direccionSeleccionada = null;
    
    // Limpiar mapa de Leaflet
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
    this.marker = null;
  }

initMap(): void {
  if (!this.direccionSeleccionada || !this.direccionSeleccionada.coordenadas) return;
  const coords = this.direccionSeleccionada.coordenadas.split(',');
  const lat = parseFloat(coords[0].trim());
  const lng = parseFloat(coords[1].trim());

  this.map = new google.maps.Map(document.getElementById('modalMapCliente'), {
    center: { lat, lng },
    zoom: 16,
    mapTypeId: 'roadmap',
    zoomControl: true
  });

  this.marker = new google.maps.Marker({
    position: { lat, lng },
    map: this.map,
    title: this.direccionSeleccionada.calle,
    animation: google.maps.Animation.DROP
  });
}


  // ⭐ NUEVO: Abrir en Google Maps externo
  abrirEnGoogleMaps(): void {
    if (this.direccionSeleccionada && this.direccionSeleccionada.coordenadas) {
      const url = `https://www.google.com/maps/search/?api=1&query=${this.direccionSeleccionada.coordenadas}`;
      window.open(url, '_blank');
    }
  }
}
