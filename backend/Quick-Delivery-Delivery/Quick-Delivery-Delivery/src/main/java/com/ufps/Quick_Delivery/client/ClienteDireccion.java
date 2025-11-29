package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;
import java.util.UUID;

@FeignClient(name = "direccion-service", url = "http://localhost:8083")
public interface ClienteDireccion {

    @GetMapping("/api/direcciones/{id}")
    DireccionResponse obtenerDireccionPorId(@PathVariable("id") UUID id);

    @PostMapping("/api/direcciones/{id}/ubicacion")
    ResponseEntity<Void> actualizarUbicacion(@PathVariable("id") UUID id,
            @RequestParam("coordenadas") String coordenadas);

    @Data
    class DireccionResponse {
        private UUID id;
        private UUID usuarioId;
        private String calle;
        private String referencia;
        private String ciudad;
        private String barrio;
        private String coordenadas;
        private String tipoReferencia;
    }
}
