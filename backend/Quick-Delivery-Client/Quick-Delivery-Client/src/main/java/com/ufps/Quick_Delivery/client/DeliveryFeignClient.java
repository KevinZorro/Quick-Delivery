package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.NuevaCalificacionRepartidorDto;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@FeignClient(name = "delivery-service", url = "${delivery-service.url}")
public interface DeliveryFeignClient {

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
        private String codigoConfirmacion;
    }

    // ---------- Calificación repartidor ----------
    @PostMapping("/api/delivery/{repartidorId}/calificaciones")
    void enviarCalificacion(@PathVariable("repartidorId") UUID repartidorId,
                            @RequestBody NuevaCalificacionRepartidorDto dto);
}
