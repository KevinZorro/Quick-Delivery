package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.client.RestauranteFeignClient;
import com.ufps.Quick_Delivery.dto.ResenaRestauranteDto;
import com.ufps.Quick_Delivery.service.ResenaRestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class ResenaRestauranteController {

    private final ResenaRestauranteService resenaRestauranteService;
    private final RestauranteFeignClient restauranteFeignClient;

    // HU042: Cliente califica restaurante
    @PostMapping("/{pedidoId}/calificar-restaurante")
    public ResponseEntity<Void> calificarPedido(@PathVariable UUID pedidoId,
                                                @RequestParam int calificacion,
                                                @RequestParam(required = false) String comentario) {
        resenaRestauranteService.calificarRestaurante(pedidoId, calificacion, comentario);
        return ResponseEntity.ok().build();
    }

    // HU016: Cliente consulta opiniones del restaurante
    @GetMapping("/restaurantes/{restauranteId}/reseñas")
    public ResponseEntity<List<ResenaRestauranteDto>> verResenasRestaurante(@PathVariable UUID restauranteId) {
        List<ResenaRestauranteDto> resenas = restauranteFeignClient.obtenerResenasRestaurante(restauranteId);
        return ResponseEntity.ok(resenas);
    }
}
