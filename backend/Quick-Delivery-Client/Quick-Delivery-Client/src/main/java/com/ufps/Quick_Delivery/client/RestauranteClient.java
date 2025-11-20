package com.ufps.Quick_Delivery.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ufps.Quick_Delivery.dto.RestauranteDto;

@FeignClient(name = "restaurante-service", url ="${restaurante-service.url}")
public interface RestauranteClient {
    @GetMapping("/api/restaurante/{id}")
    RestauranteDto obtenerRestaurantePorId(@PathVariable("id") UUID id);
}
