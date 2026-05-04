package com.ufps.Quick_Delivery.client;

import com.ufps.Quick_Delivery.dto.DireccionResponse;
import com.ufps.Quick_Delivery.dto.DistanceMatrixRequest;
import com.ufps.Quick_Delivery.dto.DistanceMatrixResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "edge-service", url = "http://localhost:8083")
public interface EdgeClient {

    // Desde tu ClienteDireccion original
    @GetMapping("/api/direcciones/{id}")
    DireccionResponse obtenerDireccionPorId(@PathVariable("id") UUID id);

    @GetMapping("/api/direcciones/usuario/{usuarioId}/ubicacion-actual")
    DireccionResponse obtenerDireccionActualUsuario(@PathVariable("usuarioId") UUID usuarioId);

    @PostMapping("/api/direcciones/{id}/ubicacion")
    ResponseEntity<Void> actualizarUbicacion(@PathVariable("id") UUID id,
                                             @RequestParam("coordenadas") String coordenadas);

    // Métodos tipo "edge" que aparecían en otros feigns (con ResponseEntity)
    @GetMapping("/api/direcciones/{id}")
    ResponseEntity<DireccionResponse> obtenerDireccion(@PathVariable("id") UUID id);

    @GetMapping("/api/direcciones/usuario/{usuarioId}")
    ResponseEntity<List<DireccionResponse>> obtenerDireccionesPorUsuario(@PathVariable("usuarioId") UUID usuarioId);

    // Maps proxied por edge (según tus snippets)
    @PostMapping("/api/maps/distance")
    ResponseEntity<DistanceMatrixResponse> calcularDistancia(@RequestBody DistanceMatrixRequest request);
}
