package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.Data;

import java.util.UUID;

@FeignClient(name = "delivery-service", url = "${delivery-service.url}")
public interface DeliveryClient {

    @PostMapping("/api/pedidos-notificaciones")
    NotificacionResponse crearNotificacionPedido(@RequestBody NotificacionRequest request);

    @Data
    class NotificacionRequest {
        private UUID pedidoId;
        private UUID clienteId;
        private UUID restauranteId;
        private UUID direccionEntregaId;
    }

    @Data
    class NotificacionResponse {
        private UUID id;
        private UUID pedidoId;
        private UUID clienteId;
        private UUID restauranteId;
        private UUID direccionEntregaId;
        private String estado;
    }
}

