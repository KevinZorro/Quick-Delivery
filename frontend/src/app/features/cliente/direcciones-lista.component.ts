import { Component, OnInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { DireccionService, Direccion } from './direccion.service';
import { HeaderComponent } from './header.component';


declare var google: any;


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

  private platformId = inject(PLATFORM_ID);

  modalMapaAbierto = false;
  direccionSeleccionada: Direccion | null = null;
  map: any;
  marker: any;
  geocoder: any; // ✅ NUEVO: Para buscar direcciones


  constructor(
    private direccionService: DireccionService,
    private router: Router
  ) {}


  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
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
    if (this.direcciones.length >= 5) {
      alert('Has alcanzado el límite máximo de 5 direcciones. Elimina una dirección existente antes de agregar una nueva.');
      return;
    }
    this.router.navigate(['/cliente/direcciones/nueva']);
  }

  // ✅ MEJORADO: Abrir modal con mapa (busca automáticamente si no hay coordenadas)
  abrirModalMapa(direccion: Direccion): void {
    this.direccionSeleccionada = direccion;
    this.modalMapaAbierto = true;

    setTimeout(() => {
      const mapElement = document.getElementById('modalMapCliente');
      if (mapElement) {
        this.initMap();
      } else {
        alert('Error al cargar el mapa. Intenta de nuevo.');
      }
    }, 200);
  }

  cerrarModalMapa(): void {
    this.modalMapaAbierto = false;
    this.direccionSeleccionada = null;
    
    if (this.map) {
      this.map = null;
    }
    this.marker = null;
  }

  // ✅ MEJORADO: Inicializar mapa con geocodificación automática
  initMap(): void {
    if (!this.direccionSeleccionada) return;

    // Inicializar geocoder
    this.geocoder = new google.maps.Geocoder();

    // ✅ Caso 1: Si tiene coordenadas, usarlas directamente
    if (this.direccionSeleccionada.coordenadas && this.direccionSeleccionada.coordenadas.trim() !== '') {
      const coords = this.direccionSeleccionada.coordenadas.split(',');
      const lat = parseFloat(coords[0].trim());
      const lng = parseFloat(coords[1].trim());

      this.crearMapa(lat, lng);
      return;
    }

    // ✅ Caso 2: Si NO tiene coordenadas, buscar por dirección
    this.buscarDireccionEnMapa();
  }

  // ✅ NUEVO: Buscar dirección automáticamente usando Google Geocoding
  buscarDireccionEnMapa(): void {
    if (!this.direccionSeleccionada) return;

    // Construir dirección completa
    const direccionCompleta = `${this.direccionSeleccionada.calle}, ${this.direccionSeleccionada.ciudad}, Colombia`;
    
    console.log('🔍 Buscando dirección:', direccionCompleta);

    this.geocoder.geocode({ address: direccionCompleta }, (results: any, status: any) => {
      if (status === 'OK' && results[0]) {
        const location = results[0].geometry.location;
        const lat = location.lat();
        const lng = location.lng();

        console.log('✅ Dirección encontrada:', lat, lng);

        // Crear mapa en la ubicación encontrada
        this.crearMapa(lat, lng);

        // ⭐ OPCIONAL: Guardar las coordenadas en la base de datos
        // this.actualizarCoordenadasDireccion(lat, lng);
      } else {
        console.error('❌ No se pudo encontrar la dirección:', status);
        alert(`No se pudo ubicar la dirección: ${direccionCompleta}\n\nVerifica que esté correctamente escrita.`);
        
        // Mapa por defecto en Bogotá
        this.crearMapa(4.7110, -74.0721);
      }
    });
  }

  // ✅ NUEVO: Crear mapa en coordenadas específicas
  crearMapa(lat: number, lng: number): void {
    this.map = new google.maps.Map(document.getElementById('modalMapCliente'), {
      center: { lat, lng },
      zoom: 16,
      mapTypeId: 'roadmap',
      zoomControl: true,
      streetViewControl: true,
      fullscreenControl: true
    });

    this.marker = new google.maps.Marker({
      position: { lat, lng },
      map: this.map,
      title: this.direccionSeleccionada?.calle || 'Ubicación',
      animation: google.maps.Animation.DROP
    });

    // ✅ Info window con la dirección
    const infoWindow = new google.maps.InfoWindow({
      content: `
        <div style="padding: 8px;">
          <h3 style="margin: 0 0 8px 0; font-size: 14px; font-weight: bold;">
            ${this.direccionSeleccionada?.calle || 'Ubicación'}
          </h3>
          <p style="margin: 0; font-size: 12px; color: #666;">
            ${this.direccionSeleccionada?.ciudad}, Colombia
          </p>
          <p style="margin: 4px 0 0 0; font-size: 11px; color: #999;">
            Lat: ${lat.toFixed(6)}, Lng: ${lng.toFixed(6)}
          </p>
        </div>
      `
    });

    this.marker.addListener('click', () => {
      infoWindow.open(this.map, this.marker);
    });

    // Abrir automáticamente el info window
    infoWindow.open(this.map, this.marker);
  }

// ✅ CORREGIDO: Actualizar coordenadas en la base de datos
actualizarCoordenadasDireccion(lat: number, lng: number): void {
  if (!this.direccionSeleccionada || !this.direccionSeleccionada.id) {
    console.error('❌ No hay dirección seleccionada o falta el ID');
    return;
  }

  const coordenadas = `${lat},${lng}`;
  
  // Actualizar en el objeto local
  this.direccionSeleccionada.coordenadas = coordenadas;

  // Actualizar en la base de datos
  this.direccionService.actualizarDireccion(this.direccionSeleccionada.id, {
    ...this.direccionSeleccionada,
    coordenadas
  }).subscribe({
    next: () => {
      console.log('✅ Coordenadas guardadas en la base de datos');
      // Recargar direcciones para actualizar la vista
      this.loadDirecciones();
    },
    error: (err) => {
      console.error('❌ Error al guardar coordenadas:', err);
    }
  });
}


  abrirEnGoogleMaps(): void {
    if (!this.direccionSeleccionada) return;

    let url: string;

    // Si tiene coordenadas, usarlas
    if (this.direccionSeleccionada.coordenadas && this.direccionSeleccionada.coordenadas.trim() !== '') {
      url = `https://www.google.com/maps/search/?api=1&query=${this.direccionSeleccionada.coordenadas}`;
    } else {
      // Si no, buscar por dirección
      const direccionCompleta = `${this.direccionSeleccionada.calle}, ${this.direccionSeleccionada.ciudad}, Colombia`;
      url = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(direccionCompleta)}`;
    }

    window.open(url, '_blank');
  }
}
