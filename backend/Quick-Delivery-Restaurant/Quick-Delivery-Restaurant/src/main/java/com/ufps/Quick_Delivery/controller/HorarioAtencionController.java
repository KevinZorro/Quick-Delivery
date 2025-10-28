package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.AuthResponse;
import com.ufps.Quick_Delivery.dto.HorarioAtencionRequest;
import com.ufps.Quick_Delivery.model.HorarioAtencion;
import com.ufps.Quick_Delivery.service.HorarioAtencionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/horario-atencion")
public class HorarioAtencionController {

    private final HorarioAtencionService service;

    public HorarioAtencionController(HorarioAtencionService service) {
        this.service = service;
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<HorarioAtencion>> listarPorRestaurante(
            @PathVariable UUID restauranteId) {
        return ResponseEntity.ok(service.listarPorRestaurante(restauranteId));
    }

    @GetMapping("/restaurante/{restauranteId}/dia/{dia}")
    public ResponseEntity<List<HorarioAtencion>> listarPorRestauranteYDia(
            @PathVariable UUID restauranteId,
            @PathVariable DayOfWeek dia) {
        return ResponseEntity.ok(service.listarPorRestauranteYDia(restauranteId, dia));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioAtencion> obtenerPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody HorarioAtencionRequest req) {
        try {
            HorarioAtencion creado = service.crear(req);
            return ResponseEntity.created(URI.create("/api/horario-atencion/" + creado.getId()))
                    .body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable UUID id,
            @RequestBody HorarioAtencionRequest req) {
        try {
            HorarioAtencion actualizado = service.actualizar(id, req);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AuthResponse> eliminar(@PathVariable UUID id) {
        boolean eliminado = service.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok(new AuthResponse("Horario eliminado correctamente"));
        }
        return ResponseEntity.notFound().build();
    }
}
