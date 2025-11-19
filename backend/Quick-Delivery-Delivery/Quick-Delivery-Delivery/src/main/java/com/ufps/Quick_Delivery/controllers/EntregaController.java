package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.AceptarPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.EntregaDto;
import com.ufps.Quick_Delivery.models.EstadoEntrega;
import com.ufps.Quick_Delivery.services.EntregaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery/entregas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class EntregaController {

    private final EntregaService entregaService;

    /**
     * Aceptar un pedido y crear entrega
     * POST /api/delivery/entregas/aceptar?usuarioId={usuarioId}&notificacionId={notificacionId}
     */
    @PostMapping("/aceptar")
    public ResponseEntity<EntregaDto> aceptarPedido(
            @RequestParam("usuarioId") UUID usuarioId,
            @RequestParam("notificacionId") UUID notificacionId,
            @RequestBody(required = false) AceptarPedidoRequestDto request) {
        try {
            if (request == null) {
                request = new AceptarPedidoRequestDto();
            }
            EntregaDto entrega = entregaService.aceptarPedido(usuarioId, notificacionId, request);
            return ResponseEntity.ok(entrega);
        } catch (RuntimeException e) {
            log.error("Error al aceptar pedido: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Listar entregas de un repartidor
     * GET /api/delivery/entregas?usuarioId={usuarioId}
     */
    @GetMapping
    public ResponseEntity<List<EntregaDto>> listarEntregas(
            @RequestParam("usuarioId") UUID usuarioId) {
        try {
            List<EntregaDto> entregas = entregaService.listarEntregasPorRepartidor(usuarioId);
            return ResponseEntity.ok(entregas);
        } catch (RuntimeException e) {
            log.error("Error al listar entregas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualizar estado de una entrega
     * PATCH /api/delivery/entregas/{entregaId}/estado?estado={estado}
     */
    @PatchMapping("/{entregaId}/estado")
    public ResponseEntity<EntregaDto> actualizarEstado(
            @PathVariable("entregaId") UUID entregaId,
            @RequestParam("estado") EstadoEntrega estado) {
        try {
            EntregaDto entrega = entregaService.actualizarEstadoEntrega(entregaId, estado);
            return ResponseEntity.ok(entrega);
        } catch (RuntimeException e) {
            log.error("Error al actualizar estado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}

