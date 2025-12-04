package com.ufps.Quick_Delivery.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "restaurante-service",
    contextId = "restauranteHorarioClientCliente",
    url = "${restaurante-service.url}"
)
public interface RestauranteHorarioClient {


    @GetMapping("/api/restaurantes/horarios/disponibilidad/{restauranteId}")
    boolean estaDisponible(@PathVariable UUID restauranteId);
}