package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.NuevaResenaRestauranteDto;
import com.ufps.Quick_Delivery.dto.ResenaRestauranteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "restaurante-service-2", url = "http://localhost:8081")
public interface RestauranteFeignClient {

    // Registrar una reseña/opinión del restaurante
    @PostMapping("/api/restaurantes/{restauranteId}/resenas")
    void crearResenaRestaurante(@PathVariable("restauranteId") UUID restauranteId,
                                @RequestBody NuevaResenaRestauranteDto dto);

    // Consultar todas las reseñas de un restaurante
    @GetMapping("/api/restaurantes/{restauranteId}/resenas")
    List<ResenaRestauranteDto> obtenerResenasRestaurante(@PathVariable("restauranteId") UUID restauranteId);
}
