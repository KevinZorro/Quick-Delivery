package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.Data;

import java.util.UUID;

@FeignClient(name = "edge-service", url = "${edge-service.url}")
public interface EdgeClient {

    /**
     * Obtener una dirección por ID
     */
    @GetMapping("/api/direcciones/{id}")
    DireccionResponse obtenerDireccion(@PathVariable("id") UUID id);

    /**
     * Obtener direcciones por usuarioId
     */
    @GetMapping("/api/direcciones/usuario/{usuarioId}")
    java.util.List<DireccionResponse> obtenerDireccionesPorUsuario(@PathVariable("usuarioId") UUID usuarioId);

    /**
     * Calcular distancia entre dos puntos usando Google Maps
     */
    @PostMapping("/api/maps/distance")
    DistanceMatrixResponse calcularDistancia(@RequestBody DistanceMatrixRequest request);

    @Data
    class DireccionResponse {
        private UUID id;
        private UUID usuarioId;
        private String calle;
        private String referencia;
        private String ciudad;
        private String barrio;
        private String coordenadas; // Formato: "lat,lng"
        private String tipoReferencia;
    }

    @Data
    class DistanceMatrixRequest {
        private String originLat;
        private String originLng;
        private String destinationLat;
        private String destinationLng;
    }

    @Data
    class DistanceMatrixResponse {
        private String origin;
        private String destination;
        private String distance; // "5.2 km"
        private String duration; // "15 mins"
        private Long distanceValue; // metros
        private Long durationValue; // segundos
    }
}

