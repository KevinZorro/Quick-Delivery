package com.ufps.Quick_Delivery.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.UUID;

@FeignClient(name = "delivery-service", url = "${delivery-service.url}")
public interface DeliveryClient {

    @PostMapping("/api/entregas/iniciar")
    EntregaResponse iniciarEntrega(@RequestBody IniciarEntregaRequest request);

    @Data
    class IniciarEntregaRequest {
        private UUID pedidoId;
        private UUID repartidorId;
    }

    @Data
    class EntregaResponse {
        private UUID id;
        private UUID pedidoId;
        private UUID repartidorId;
        private String estado;
        private LocalDateTime horaInicio;
        private String codigoConfirmacion; // ⭐ Código que quieres mostrar al cliente
    }
}
