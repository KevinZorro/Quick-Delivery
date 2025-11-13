package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.AceptarPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionConDistanciaDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionRequestDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionResponseDto;
import com.ufps.Quick_Delivery.dto.UbicacionRepartidorDto;
import com.ufps.Quick_Delivery.services.PedidoNotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos-notificaciones")
@RequiredArgsConstructor
@Slf4j
public class PedidoNotificacionController {

    private final PedidoNotificacionService service;

    /**
     * Logging de inicialización del controlador
     */
    @PostConstruct
    public void init() {
        log.info("✅ PedidoNotificacionController inicializado - Endpoint: POST /api/pedidos-notificaciones");
    }

    /**
     * Endpoint para recibir notificaciones de pedidos desde el servicio Cliente
     * POST /api/pedidos-notificaciones
     */
    @PostMapping
    public ResponseEntity<PedidoNotificacionResponseDto> crearNotificacion(
            @Valid @RequestBody PedidoNotificacionRequestDto request) {
        try {
            log.info("📨 Recibiendo notificación de pedido desde servicio Cliente - pedidoId: {}, clienteId: {}, restauranteId: {}", 
                    request.getPedidoId(), request.getClienteId(), request.getRestauranteId());
            PedidoNotificacionResponseDto response = service.crearNotificacion(request);
            log.info("✅ Notificación creada exitosamente - id: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("❌ Error al crear notificación: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener todas las notificaciones pendientes (para que los repartidores las vean)
     * GET /api/pedidos-notificaciones/pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<PedidoNotificacionResponseDto>> obtenerNotificacionesPendientes() {
        log.info("🔍 Obteniendo notificaciones pendientes");
        List<PedidoNotificacionResponseDto> notificaciones = service.obtenerNotificacionesPendientes();
        return ResponseEntity.ok(notificaciones);
    }

    /**
     * Obtener notificaciones pendientes filtradas por ubicación GPS del repartidor
     * Solo muestra pedidos dentro del rango definido
     * GET /api/pedidos-notificaciones/pendientes-por-ubicacion?lat=7.889&lng=-72.498&rangoMaximoKm=10
     */
    @GetMapping("/pendientes-por-ubicacion")
    public ResponseEntity<List<PedidoNotificacionConDistanciaDto>> obtenerNotificacionesPendientesPorUbicacion(
            @RequestParam("lat") Double latitud,
            @RequestParam("lng") Double longitud,
            @RequestParam(value = "rangoMaximoKm", defaultValue = "10.0") Double rangoMaximoKm) {
        
        log.info("🔍 Obteniendo notificaciones pendientes por ubicación: lat={}, lng={}, rango={}km", 
                latitud, longitud, rangoMaximoKm);
        
        UbicacionRepartidorDto ubicacion = new UbicacionRepartidorDto();
        ubicacion.setLatitud(latitud);
        ubicacion.setLongitud(longitud);
        ubicacion.setRangoMaximoKm(rangoMaximoKm);
        
        List<PedidoNotificacionConDistanciaDto> notificaciones = 
                service.obtenerNotificacionesPendientesPorUbicacion(ubicacion);
        return ResponseEntity.ok(notificaciones);
    }

    /**
     * Obtener una notificación por ID de pedido
     * GET /api/pedidos-notificaciones/pedido/{pedidoId}
     */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PedidoNotificacionResponseDto> obtenerPorPedidoId(@PathVariable UUID pedidoId) {
        return service.obtenerPorPedidoId(pedidoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Aceptar un pedido (solo para repartidores)
     * POST /api/pedidos-notificaciones/{notificacionId}/aceptar
     */
    @PostMapping("/{notificacionId}/aceptar")
    public ResponseEntity<?> aceptarPedido(
            @PathVariable UUID notificacionId,
            @Valid @RequestBody AceptarPedidoRequestDto request) {
        try {
            log.info("🚚 Aceptando pedido: notificacionId={}, repartidorId={}", 
                    notificacionId, request.getRepartidorId());
            PedidoNotificacionResponseDto response = service.aceptarPedido(notificacionId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("❌ Error al aceptar pedido: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error al aceptar el pedido: " + e.getMessage());
        }
    }

    /**
     * Obtener pedidos aceptados por un repartidor
     * GET /api/pedidos-notificaciones/repartidor/{repartidorId}/aceptados
     */
    @GetMapping("/repartidor/{repartidorId}/aceptados")
    public ResponseEntity<List<PedidoNotificacionResponseDto>> obtenerPedidosAceptados(
            @PathVariable UUID repartidorId) {
        log.info("🔍 Obteniendo pedidos aceptados por repartidor: {}", repartidorId);
        List<PedidoNotificacionResponseDto> pedidos = service.obtenerPedidosAceptadosPorRepartidor(repartidorId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtener todas las notificaciones (para administración)
     * GET /api/pedidos-notificaciones
     */
    @GetMapping
    public ResponseEntity<List<PedidoNotificacionResponseDto>> obtenerTodas() {
        List<PedidoNotificacionResponseDto> notificaciones = service.obtenerTodas();
        return ResponseEntity.ok(notificaciones);
    }
}

