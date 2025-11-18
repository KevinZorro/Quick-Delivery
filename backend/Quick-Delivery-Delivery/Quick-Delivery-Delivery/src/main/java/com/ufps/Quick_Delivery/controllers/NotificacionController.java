package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.NotificacionPedidoDto;
import com.ufps.Quick_Delivery.dto.PedidoNotificacionRequestDto;
import com.ufps.Quick_Delivery.models.PedidoNotificacion;
import com.ufps.Quick_Delivery.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class NotificacionController {

    private final NotificacionService notificacionService;

    /**
     * Recibir notificación de nuevo pedido desde Client
     * POST /api/delivery/notificaciones
     */
    @PostMapping
    public ResponseEntity<PedidoNotificacion> recibirNotificacion(
            @RequestBody PedidoNotificacionRequestDto request) {
        try {
            PedidoNotificacion notificacion = notificacionService.recibirNotificacionPedido(request);
            return ResponseEntity.ok(notificacion);
        } catch (Exception e) {
            log.error("Error al recibir notificación: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtener notificaciones disponibles para un repartidor
     * GET /api/delivery/notificaciones/disponibles?usuarioId={usuarioId}
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<NotificacionPedidoDto>> obtenerNotificacionesDisponibles(
            @RequestParam("usuarioId") UUID usuarioId) {
        try {
            List<NotificacionPedidoDto> notificaciones = notificacionService.obtenerNotificacionesDisponibles(usuarioId);
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}

