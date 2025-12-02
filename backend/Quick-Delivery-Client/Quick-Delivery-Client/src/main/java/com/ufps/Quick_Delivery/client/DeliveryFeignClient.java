package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.NuevaCalificacionRepartidorDto;
import com.ufps.Quick_Delivery.client.IniciarEntregaRequest;
import com.ufps.Quick_Delivery.client.EntregaResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "delivery-service", url = "${delivery-service.url}")
public interface DeliveryFeignClient {

    @PostMapping("/api/entregas/iniciar")
    EntregaResponse iniciarEntrega(@RequestBody IniciarEntregaRequest request);

    @PostMapping("/api/delivery/{repartidorId}/calificaciones")
    void enviarCalificacion(@PathVariable("repartidorId") UUID repartidorId,
                            @RequestBody NuevaCalificacionRepartidorDto dto);
}
