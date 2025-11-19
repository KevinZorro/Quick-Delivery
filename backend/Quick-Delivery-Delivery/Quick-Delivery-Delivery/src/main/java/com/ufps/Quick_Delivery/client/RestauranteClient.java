package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@FeignClient(name = "restaurante-direccion-service", url = "${edge-service.url}/api/direcciones")
public interface RestauranteClient {

    @GetMapping("/restaurante/{restauranteId}")
    ResponseEntity<DireccionResponse> obtenerDireccionRestaurante(@PathVariable("restauranteId") UUID restauranteId);
}

