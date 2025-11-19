import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { DireccionRestauranteService, DireccionRestaurante } from './direcciones-restaurante.service';


declare var google: any; // ⭐ Declarar Google Maps


@Component({
  selector: 'app-direcciones-restaurante-lista',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './direcciones-restaurante-lista.component.html'
})
export class DireccionesRestauranteListaComponent implements OnInit {
  direcciones: DireccionRestaurante[] = [];
  loading = true;
  errorMessage: string | null = null;
  usuarioId: string = '';

  private platformId = inject(PLATFORM_ID);

  // ⭐ Variables para el modal del mapa
  modalMapaAbierto = false;
  direccionSeleccionada: DireccionRestaurante | null = null;
  map: any;
  marker: any;


  constructor(
    private direccionService: DireccionRestauranteService,
    private router: Router
  ) {}


  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const restauranteId = localStorage.getItem('quick-delivery-userId');
      
      if (!restauranteId) {
        this.errorMessage = 'Error: No se pudo identificar el restaurante.';
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
        return;
      }

      this.usuarioId = restauranteId;
      this.loadDirecciones();
    }
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
    this.router.navigate(['/restaurante/direcciones/editar', id]);
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
    if (this.direcciones.length >= 5) {
      alert('Has alcanzado el límite máximo de 5 direcciones.');
      return;
    }
    this.router.navigate(['/restaurante/direcciones/nueva']);
  }

  // ⭐ ACTUALIZADO: Abrir modal con mapa
  abrirModalMapa(direccion: DireccionRestaurante): void {
    if (!direccion.coordenadas || direccion.coordenadas.trim() === '') {
      alert('Esta dirección no tiene coordenadas');
      return;
    }

    this.direccionSeleccionada = direccion;
    this.modalMapaAbierto = true;

    // ⭐ AUMENTAR TIMEOUT Y VERIFICAR ELEMENTO
    setTimeout(() => {
      const mapElement = document.getElementById('modalMap');
      
      if (mapElement) {
        console.log('Elemento del mapa encontrado, inicializando...');
        this.initMap();
      } else {
        console.error('Elemento modalMap no encontrado');
        alert('Error al cargar el mapa. Por favor intenta de nuevo.');
      }
    }, 300); // ⭐ Aumentado a 300ms
  }

  // ⭐ NUEVO: Cerrar modal
  cerrarModalMapa(): void {
    this.modalMapaAbierto = false;
    this.direccionSeleccionada = null;
    this.map = null;
    this.marker = null;
  }

  // ⭐ ACTUALIZADO: Inicializar mapa con verificación
  initMap(): void {
    if (!this.direccionSeleccionada || !this.direccionSeleccionada.coordenadas) {
      console.error('No hay dirección seleccionada o coordenadas');
      return;
    }

    const coords = this.direccionSeleccionada.coordenadas.split(',');
    const lat = parseFloat(coords[0].trim());
    const lng = parseFloat(coords[1].trim());

    console.log('Inicializando mapa con coordenadas:', lat, lng);

    try {
      // Crear el mapa
      this.map = new google.maps.Map(document.getElementById('modalMap'), {
        center: { lat, lng },
        zoom: 16,
        mapTypeId: 'roadmap',
        disableDefaultUI: false,
        zoomControl: true,
        streetViewControl: false
      });

      // Crear marcador
      this.marker = new google.maps.Marker({
        position: { lat, lng },
        map: this.map,
        title: this.direccionSeleccionada.calle,
        animation: google.maps.Animation.DROP
      });

      console.log('Mapa y marcador creados exitosamente');
    } catch (error) {
      console.error('Error al crear el mapa:', error);
      alert('Error al inicializar el mapa de Google');
    }
  }

  // ⭐ OPCIONAL: Obtener icono según tipo (comentado por si causa problemas)
  getIconForTipo(tipo: string): string {
    const icons: { [key: string]: string } = {
      'SEDE_PRINCIPAL': 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
      'SUCURSAL': 'http://maps.google.com/mapfiles/ms/icons/orange-dot.png',
      'COCINA_CENTRAL': 'http://maps.google.com/mapfiles/ms/icons/purple-dot.png'
    };
    return icons[tipo] || 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png';
  }

  // ⭐ NUEVO: Abrir en Google Maps externo
  abrirEnGoogleMaps(): void {
    if (this.direccionSeleccionada && this.direccionSeleccionada.coordenadas) {
      const url = `https://www.google.com/maps/search/?api=1&query=${this.direccionSeleccionada.coordenadas}`;
      window.open(url, '_blank');
    }
  }
}
