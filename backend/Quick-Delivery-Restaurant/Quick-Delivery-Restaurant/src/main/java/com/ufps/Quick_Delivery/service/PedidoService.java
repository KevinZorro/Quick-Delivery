package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.PedidoClient;
import com.ufps.Quick_Delivery.client.PedidoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoClient pedidoClient;

    /**
     * Obtener pedidos pendientes (INICIADO) de un restaurante
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPedidosPendientes(UUID restauranteId) {
        log.info("Obteniendo pedidos pendientes para restaurante: {}", restauranteId);

        try {
            // Obtener todos los pedidos del servicio Client
            ResponseEntity<List<PedidoResponse>> response = pedidoClient.listarPedidos();
            
            if (response == null || response.getBody() == null) {
                log.warn("No se obtuvieron pedidos del servicio Client");
                return List.of();
            }

            List<PedidoResponse> todosLosPedidos = response.getBody();
            if (todosLosPedidos == null) {
                log.warn("La lista de pedidos es null");
                return List.of();
            }

            // Filtrar pedidos del restaurante con estado INICIADO
            List<PedidoResponse> pedidosPendientes = todosLosPedidos.stream()
                    .filter(pedido -> pedido.getRestauranteId() != null 
                            && pedido.getRestauranteId().equals(restauranteId)
                            && "INICIADO".equalsIgnoreCase(pedido.getEstado()))
                    .collect(Collectors.toList());

            log.info("Encontrados {} pedidos pendientes para restaurante {}", pedidosPendientes.size(), restauranteId);
            return pedidosPendientes;

        } catch (Exception e) {
            log.error("Error al obtener pedidos pendientes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener pedidos pendientes: " + e.getMessage(), e);
        }
    }

    /**
     * Aceptar un pedido (cambiar estado a EN_COCINA)
     */
    @Transactional
    public PedidoResponse aceptarPedido(UUID restauranteId, UUID pedidoId) {
        log.info("Restaurante {} aceptando pedido {}", restauranteId, pedidoId);

        // Validar que el pedido existe y pertenece al restaurante
        ResponseEntity<PedidoResponse> pedidoResponse = pedidoClient.obtenerPedido(pedidoId);
        PedidoResponse pedido = pedidoResponse.getBody();

        if (pedido == null) {
            throw new RuntimeException("Pedido no encontrado");
        }

        // Validar que el pedido pertenece al restaurante
        if (!pedido.getRestauranteId().equals(restauranteId)) {
            throw new RuntimeException("El pedido no pertenece a este restaurante");
        }

        // Validar que el estado es INICIADO
        if (!"INICIADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new RuntimeException("Solo se pueden aceptar pedidos con estado INICIADO. Estado actual: " + pedido.getEstado());
        }

        // Cambiar estado a EN_COCINA
        ResponseEntity<PedidoResponse> updatedResponse = pedidoClient.cambiarEstado(pedidoId, "EN_COCINA");
        
        if (updatedResponse == null || updatedResponse.getBody() == null) {
            throw new RuntimeException("Error al actualizar el estado del pedido");
        }

        log.info("Pedido {} aceptado exitosamente por restaurante {}", pedidoId, restauranteId);
        return updatedResponse.getBody();
    }

    /**
     * Rechazar un pedido (cambiar estado a RECHAZADO_POR_RESTAURANTE)
     */
    @Transactional
    public PedidoResponse rechazarPedido(UUID restauranteId, UUID pedidoId) {
        log.info("Restaurante {} rechazando pedido {}", restauranteId, pedidoId);

        // Validar que el pedido existe y pertenece al restaurante
        ResponseEntity<PedidoResponse> pedidoResponse = pedidoClient.obtenerPedido(pedidoId);
        PedidoResponse pedido = pedidoResponse.getBody();

        if (pedido == null) {
            throw new RuntimeException("Pedido no encontrado");
        }

        // Validar que el pedido pertenece al restaurante
        if (!pedido.getRestauranteId().equals(restauranteId)) {
            throw new RuntimeException("El pedido no pertenece a este restaurante");
        }

        // Validar que el estado es INICIADO
        if (!"INICIADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new RuntimeException("Solo se pueden rechazar pedidos con estado INICIADO. Estado actual: " + pedido.getEstado());
        }

        // Cambiar estado a RECHAZADO_POR_RESTAURANTE
        ResponseEntity<PedidoResponse> updatedResponse = pedidoClient.cambiarEstado(pedidoId, "RECHAZADO_POR_RESTAURANTE");
        
        if (updatedResponse == null || updatedResponse.getBody() == null) {
            throw new RuntimeException("Error al actualizar el estado del pedido");
        }

        log.info("Pedido {} rechazado exitosamente por restaurante {}", pedidoId, restauranteId);
        return updatedResponse.getBody();
    }
}

