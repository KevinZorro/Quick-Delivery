// src/main/java/com/ufps/Quick_Delivery/controller/ResenaRestauranteController.java
package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.NuevaResenaRestauranteDto;
import com.ufps.Quick_Delivery.model.ResenaRestaurante;
import com.ufps.Quick_Delivery.service.ResenaRestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/resenas")
@RequiredArgsConstructor
public class ResenaRestauranteController {

    private final ResenaRestauranteService service;

    // HU042: Cliente califica restaurante
    @PostMapping
    public ResponseEntity<Void> crearResena(@PathVariable UUID restauranteId,
                                            @Valid @RequestBody NuevaResenaRestauranteDto dto) {
        dto.setRestauranteId(restauranteId);
        service.registrarResena(dto);
        return ResponseEntity.ok().build();
    }

    // HU016: Ver opiniones del restaurante
    @GetMapping
    public ResponseEntity<List<ResenaRestaurante>> listarResenas(@PathVariable UUID restauranteId) {
        List<ResenaRestaurante> opiniones = service.listarOpinionesRestaurante(restauranteId);
        return ResponseEntity.ok(opiniones);
    }
}
