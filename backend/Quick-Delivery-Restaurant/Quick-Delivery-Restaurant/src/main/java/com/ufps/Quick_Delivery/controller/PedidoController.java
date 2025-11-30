package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.client.PedidoResponse;
import com.ufps.Quick_Delivery.dto.RestauranteResponseDto;
import com.ufps.Quick_Delivery.service.PedidoService;
import com.ufps.Quick_Delivery.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurante")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;
    private final RestauranteService restauranteService;

    /**
     * Obtener pedidos pendientes del restaurante autenticado
     * GET /api/restaurante/pedidos/pendientes
     */
    @GetMapping("/pedidos/pendientes")
    public ResponseEntity<?> obtenerPedidosPendientes(Authentication authentication) {
        try {
            UUID usuarioId = UUID.fromString(authentication.getName());
            log.info("Solicitando pedidos pendientes para usuario: {}", usuarioId);
            
            // Obtener restaurante por usuarioId
            RestauranteResponseDto restaurante = restauranteService.obtenerPorUsuarioId(usuarioId);
            UUID restauranteId = restaurante.getId();
            
            List<PedidoResponse> pedidos = pedidoService.obtenerPedidosPendientes(restauranteId);
            return ResponseEntity.ok(pedidos);
            
        } catch (RuntimeException e) {
            log.error("Error al obtener pedidos pendientes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al obtener pedidos pendientes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    /**
     * Aceptar un pedido
     * POST /api/restaurante/pedido/{id}/aceptar
     */
    @PostMapping("/pedido/{id}/aceptar")
    public ResponseEntity<?> aceptarPedido(
            @PathVariable("id") UUID pedidoId,
            Authentication authentication) {
        try {
            UUID usuarioId = UUID.fromString(authentication.getName());
            log.info("Aceptando pedido {} por usuario {}", pedidoId, usuarioId);
            
            // Obtener restaurante por usuarioId
            RestauranteResponseDto restaurante = restauranteService.obtenerPorUsuarioId(usuarioId);
            UUID restauranteId = restaurante.getId();
            
            PedidoResponse pedido = pedidoService.aceptarPedido(restauranteId, pedidoId);
            return ResponseEntity.ok(pedido);
            
        } catch (RuntimeException e) {
            log.error("Error al aceptar pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al aceptar pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    /**
     * Rechazar un pedido
     * POST /api/restaurante/pedido/{id}/rechazar
     */
    @PostMapping("/pedido/{id}/rechazar")
    public ResponseEntity<?> rechazarPedido(
            @PathVariable("id") UUID pedidoId,
            Authentication authentication) {
        try {
            UUID usuarioId = UUID.fromString(authentication.getName());
            log.info("Rechazando pedido {} por usuario {}", pedidoId, usuarioId);
            
            // Obtener restaurante por usuarioId
            RestauranteResponseDto restaurante = restauranteService.obtenerPorUsuarioId(usuarioId);
            UUID restauranteId = restaurante.getId();
            
            PedidoResponse pedido = pedidoService.rechazarPedido(restauranteId, pedidoId);
            return ResponseEntity.ok(pedido);
            
        } catch (RuntimeException e) {
            log.error("Error al rechazar pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al rechazar pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}

