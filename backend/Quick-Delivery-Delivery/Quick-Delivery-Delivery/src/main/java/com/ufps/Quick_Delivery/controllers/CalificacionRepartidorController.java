package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.dto.NuevaCalificacionRepartidorDto;
import com.ufps.Quick_Delivery.dto.CalificacionRepartidorResponse;
import com.ufps.Quick_Delivery.models.CalificacionRepartidor;
import com.ufps.Quick_Delivery.services.CalificacionRepartidorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/delivery/{repartidorId}/calificaciones")
@RequiredArgsConstructor
public class CalificacionRepartidorController {

    private final CalificacionRepartidorService service;

    @PostMapping
    public ResponseEntity<Void> registrarCalificacion(@PathVariable UUID repartidorId,
                                                      @Valid @RequestBody NuevaCalificacionRepartidorDto dto) {
        service.registrarCalificacion(repartidorId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CalificacionRepartidorResponse> listarCalificaciones(@PathVariable UUID repartidorId) {
        List<CalificacionRepartidor> opiniones = service.obtenerCalificacionesRepartidor(repartidorId);
        double promedio = service.obtenerPromedioRepartidor(repartidorId);
        CalificacionRepartidorResponse resp = new CalificacionRepartidorResponse(opiniones, promedio);
        return ResponseEntity.ok(resp);
    }
}
