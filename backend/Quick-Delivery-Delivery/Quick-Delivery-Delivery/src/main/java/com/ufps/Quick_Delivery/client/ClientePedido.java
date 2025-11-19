package com.ufps.Quick_Delivery.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "pedido-service", url = "http://localhost:8080")
public interface ClientePedido {

    @GetMapping("/api/pedidos/repartidor/{repartidorId}")
    List<PedidoResponse> obtenerPedidosPorRepartidor(@PathVariable("repartidorId") UUID repartidorId);

    // 🔹 Obtener un pedido específico por su ID
    @GetMapping("/api/pedidos/{id}")
    PedidoResponse obtenerPedidoPorId(@PathVariable("id") UUID id);

    // 🔹 DTO de respuesta
    @Data
    class PedidoResponse {
        private UUID id;
        private Cliente cliente;
        private UUID restauranteId;
        private UUID repartidorId;
        private UUID direccionEntregaId;
        private int total;
        private String metodoPago;
        private String estado;
        private String preferencias;
        private LocalDateTime fechaCreacion;
        private LocalDateTime fechaActualizacion;
        private List<ItemPedidoResponse> items;
    }

    @Data
    class Cliente {
        private UUID id;
        private UUID usuarioId;
    }

    @Data
    class ItemPedidoResponse {
        private UUID id;
        private UUID productoId;
        private int cantidad;
        private int precioUnidad;
        private int subtotal;
    }

}
