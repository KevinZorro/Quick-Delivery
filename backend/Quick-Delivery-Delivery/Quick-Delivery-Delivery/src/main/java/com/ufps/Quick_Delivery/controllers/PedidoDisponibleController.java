package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.PedidoDisponibleDto;
import com.ufps.Quick_Delivery.services.PedidoDisponibleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PedidoDisponibleController {

    private final PedidoDisponibleService pedidoDisponibleService;

    @GetMapping("/disponibles")
    public ResponseEntity<?> obtenerPedidosDisponibles(
            @RequestParam("usuarioId") UUID usuarioId) {
        try {
            List<PedidoDisponibleDto> pedidos = pedidoDisponibleService.obtenerPedidosDisponibles(usuarioId);
            return ResponseEntity.ok(pedidos);
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Error en obtenerPedidosDisponibles: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Error al obtener pedidos disponibles: " + e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.status(500).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error inesperado en obtenerPedidosDisponibles: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("message", "Error inesperado: " + e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Aceptar un pedido (asignación manual)
     * POST /api/delivery/pedidos/{pedidoId}/aceptar?usuarioId={usuarioId}
     */
    @PostMapping("/{pedidoId}/aceptar")
    public ResponseEntity<?> aceptarPedido(
            @PathVariable("pedidoId") UUID pedidoId,
            @RequestParam("usuarioId") UUID usuarioId) {
        try {
            pedidoDisponibleService.aceptarPedido(usuarioId, pedidoId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error al aceptar pedido: " + e.getMessage());
        }
    }
}

