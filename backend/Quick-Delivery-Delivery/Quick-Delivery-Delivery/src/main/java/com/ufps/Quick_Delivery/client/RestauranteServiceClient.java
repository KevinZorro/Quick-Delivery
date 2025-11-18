package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@FeignClient(name = "restaurante-service-client", url = "${restaurante-service.url}/api/restaurante")
public interface RestauranteServiceClient {

    @GetMapping("/{id}")
    ResponseEntity<RestauranteResponse> obtenerRestaurante(@PathVariable("id") UUID id);
}

