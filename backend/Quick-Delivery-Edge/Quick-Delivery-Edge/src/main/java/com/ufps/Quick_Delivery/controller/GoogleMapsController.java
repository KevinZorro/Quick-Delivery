package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.*;
import com.ufps.Quick_Delivery.service.GoogleMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoogleMapsController {

    private final GoogleMapsService googleMapsService;

    /**
     * Geocodificar una dirección
     * POST /api/maps/geocode
     */
    @PostMapping("/geocode")
    public ResponseEntity<GeocodingResponseDto> geocode(@RequestBody GeocodingRequestDto request) {
        String fullAddress = request.getFullAddress();
        GeocodingResponseDto response = googleMapsService.geocodeAddress(fullAddress);
        return ResponseEntity.ok(response);
    }

    /**
     * Geocodificación inversa
     * GET /api/maps/reverse-geocode?lat=7.889&lng=-72.498
     */
    @GetMapping("/reverse-geocode")
    public ResponseEntity<GeocodingResponseDto> reverseGeocode(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        GeocodingResponseDto response = googleMapsService.reverseGeocode(lat, lng);
        return ResponseEntity.ok(response);
    }

    /**
     * Calcular distancia entre dos puntos
     * POST /api/maps/distance
     */
    @PostMapping("/distance")
    public ResponseEntity<DistanceMatrixResponseDto> calculateDistance(
            @RequestBody DistanceMatrixRequestDto request) {
        DistanceMatrixResponseDto response = googleMapsService.calculateDistance(
                request.getOriginLat(),
                request.getOriginLng(),
                request.getDestinationLat(),
                request.getDestinationLng()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Encontrar restaurante más cercano
     * POST /api/maps/nearest-restaurant
     */
    @PostMapping("/nearest-restaurant")
    public ResponseEntity<DistanceMatrixResponseDto> findNearestRestaurant(
            @RequestParam String clientLat,
            @RequestParam String clientLng,
            @RequestBody List<String> restaurantCoordinates) {
        DistanceMatrixResponseDto response = googleMapsService.findNearestRestaurant(
                clientLat, clientLng, restaurantCoordinates
        );
        return ResponseEntity.ok(response);
    }
}
