import { Component, OnInit, OnDestroy, AfterViewInit, PLATFORM_ID, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { DeliveryService, TrackingData } from './delivery.service';

declare var google: any;

@Component({
  selector: 'app-tracking-mapa',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './tracking-mapa.component.html'
})
export class TrackingMapaComponent implements OnInit, OnDestroy, AfterViewInit {

  pedidoId: string = '';
  usuarioId: string = '';
  intervalId: any = null;
  watchId: number | null = null;   // 🆕 PARA TRACKING EN TIEMPO REAL

  map: any;
  directionsService: any;
  directionsRenderer: any;
  repartidorMarker: any;
  clienteMarker: any;

  trackingData: TrackingData | null = null;
  loading = true;
  error = '';
  distanciaTexto = '';
  tiempoTexto = '';

  private platformId = inject(PLATFORM_ID);

  constructor(
    private deliveryService: DeliveryService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {

      this.usuarioId = localStorage.getItem('quick-delivery-userId') || '';
      this.pedidoId = this.route.snapshot.paramMap.get('pedidoId') || '';

      if (this.pedidoId) {
        this.cargarTrackingData();

        // 🔄 Refrescar mapa cada 5 segundos
        this.intervalId = setInterval(() => {
          this.actualizarTracking();
        }, 5000);

        // ============================
        // 🛰️ ACTIVAR TRACKING EN TIEMPO REAL
        // ============================
        this.iniciarTrackingTiempoReal();

      } else {
        this.error = 'No se encontró ID de pedido';
        this.loading = false;
      }
    }
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    if (this.intervalId) clearInterval(this.intervalId);

    if (this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
      console.log("🛑 Tracking de posición detenido");
    }
  }

  // ============================================================
  // 🛰️ TRACKING EN TIEMPO REAL DEL REPARTIDOR
  // ============================================================
  iniciarTrackingTiempoReal() {
    if (!navigator.geolocation) {
      console.log("❌ El navegador no soporta geolocación");
      return;
    }

    console.log("🟦 Iniciando watchPosition()...");

    this.watchId = navigator.geolocation.watchPosition(
      (pos) => {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;

        console.log(`📍 NUEVA UBICACIÓN DETECTADA: lat=${lat}, lng=${lng}`);

        // Enviar al backend
        this.deliveryService.actualizarUbicacion(this.usuarioId, lat, lng).subscribe({
          next: () => console.log("📤 Ubicación enviada al backend correctamente"),
          error: (err) => console.error("❌ Error enviando ubicación:", err)
        });

        // Mover marcador si ya existe
        if (this.repartidorMarker) {
          this.repartidorMarker.setPosition({ lat, lng });
          console.log("🟢 Marcador del repartidor movido en el mapa");
        }
      },
      (error) => {
        console.error("❌ Error en watchPosition:", error);
      },
      {
        enableHighAccuracy: true,
        maximumAge: 0,
        timeout: 5000
      }
    );
  }

  // ============================================================
  //              ACTUALIZACIÓN DE TRACKING DESDE EL BACKEND
  // ============================================================
  actualizarTracking() {
    this.deliveryService.obtenerTrackingData(this.pedidoId).subscribe({
      next: data => {

        // Si no hay mapa todavía, crear
        if (!this.map) {
          this.trackingData = data;
          this.initMap();
          return;
        }

        this.trackingData = data;

        console.log("🔄 Actualizando marcadores desde backend:");
        console.log(`   ➤ Repartidor: ${data.repartidorLat}, ${data.repartidorLng}`);
        console.log(`   ➤ Cliente: ${data.clienteLat}, ${data.clienteLng}`);

        // Mover marcadores
        this.repartidorMarker.setPosition({
          lat: data.repartidorLat,
          lng: data.repartidorLng
        });

        this.clienteMarker.setPosition({
          lat: data.clienteLat,
          lng: data.clienteLng
        });

        this.calcularRuta();
      },
      error: err => {
        console.error("❌ Error actualizando tracking:", err);
      }
    });
  }

  // ============================================================
  //                  CARGAR TRACKING INICIAL
  // ============================================================
  cargarTrackingData(): void {
    this.loading = true;
    this.error = '';

    this.deliveryService.obtenerTrackingData(this.pedidoId).subscribe({
      next: (data) => {
        console.log("📦 Tracking data inicial:", data);

        this.trackingData = data;
        this.loading = false;

        setTimeout(() => this.initMap(), 50);
      },
      error: (err) => {
        this.error = 'Error al cargar tracking: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  // ============================================================
  //               MAPA Y RUTAS (sin cambios)
  // ============================================================
  initMap(): void {
    console.log("🟦 Iniciando initMap()");

    if (!this.trackingData) {
      console.log("❌ trackingData no existe aún");
      return;
    }

    if (!window.hasOwnProperty('google')) {
      console.log("❌ google no está disponible todavía. Reintentando...");
      setTimeout(() => this.initMap(), 300);
      return;
    }

    console.log("🟩 Google Maps cargado correctamente");

    const mapDiv = document.getElementById('trackingMap');
    console.log("📌 mapDiv:", mapDiv);

    if (!mapDiv) {
      console.log("❌ No existe #trackingMap todavía en el DOM");
      return;
    }

    this.map = new google.maps.Map(mapDiv, {
      center: {
        lat: this.trackingData.repartidorLat,
        lng: this.trackingData.repartidorLng
      },
      zoom: 14
    });

    console.log("🟩 Mapa creado");

    this.repartidorMarker = new google.maps.Marker({
      position: {
        lat: this.trackingData.repartidorLat,
        lng: this.trackingData.repartidorLng
      },
      map: this.map,
      title: 'Repartidor',
      icon: 'https://maps.google.com/mapfiles/ms/icons/green-dot.png'
    });

    this.clienteMarker = new google.maps.Marker({
      position: {
        lat: this.trackingData.clienteLat,
        lng: this.trackingData.clienteLng
      },
      map: this.map,
      title: 'Cliente',
      icon: 'https://maps.google.com/mapfiles/ms/icons/red-dot.png'
    });

    console.log("🟩 Marcadores creados");

    this.calcularRuta();
  }

calcularRuta(): void {
  if (!this.trackingData || !this.map) {
    console.log("❌ No hay trackingData o mapa para calcular la ruta");
    return;
  }

  console.log("🛣 Calculando ruta...");

  // ===============================
  // 🚫 Evita que Google Maps resetee zoom/center
  // ===============================
  const currentCenter = this.map.getCenter();
  const currentZoom = this.map.getZoom();


  this.directionsService = new google.maps.DirectionsService();
  this.directionsRenderer = new google.maps.DirectionsRenderer({
    map: this.map,
    suppressMarkers: true,
    preserveViewport: true,   // 🔥 Fix importante
    polylineOptions: {
      strokeColor: '#1E90FF',
      strokeWeight: 6,
      strokeOpacity: 0.8
    }
  });

  const request = {
    origin: {
      lat: this.trackingData.repartidorLat,
      lng: this.trackingData.repartidorLng
    },
    destination: {
      lat: this.trackingData.clienteLat,
      lng: this.trackingData.clienteLng
    },
    travelMode: google.maps.TravelMode.DRIVING
  };

  this.directionsService.route(request, (result: any, status: any) => {
    console.log("📨 Estado ruta:", status);

    if (status === 'OK' && result) {
      this.directionsRenderer.setDirections(result);

      const leg = result.routes[0]?.legs[0];
      if (leg) {
        this.distanciaTexto = leg.distance.text;
        this.tiempoTexto = leg.duration.text;
      }

      console.log("🟩 Ruta generada con éxito");
    } else {
      console.error('❌ Error al calcular ruta:', status);
    }

    // ===============================
    // 🔥 Restaurar zoom y posición del usuario
    // ===============================
    this.map.setCenter(currentCenter);
    this.map.setZoom(currentZoom);
  });
}


  actualizarRuta(): void {
    this.cargarTrackingData();
  }

  volver(): void {
    this.router.navigate(['/delivery']);
  }
}
