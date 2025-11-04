package com.ufps.Quick_Delivery.service;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.*;
import com.ufps.Quick_Delivery.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsService {

    private final GeoApiContext geoApiContext;

    /**
     * Geocodificar una dirección (obtener coordenadas)
     */
    public GeocodingResponseDto geocodeAddress(String address) {
        try {
            log.info("Geocodificando dirección: {}", address);
            
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();
            
            if (results != null && results.length > 0) {
                GeocodingResult result = results[0];
                LatLng location = result.geometry.location;
                
                return GeocodingResponseDto.builder()
                        .address(address)
                        .latitude(location.lat)
                        .longitude(location.lng)
                        .formattedAddress(result.formattedAddress)
                        .placeId(result.placeId)
                        .build();
            }
            
            throw new RuntimeException("No se encontraron resultados para la dirección: " + address);
            
        } catch (Exception e) {
            log.error("Error al geocodificar dirección: {}", e.getMessage());
            throw new RuntimeException("Error en geocodificación: " + e.getMessage());
        }
    }

    /**
     * Geocodificación inversa (coordenadas a dirección)
     */
    public GeocodingResponseDto reverseGeocode(double latitude, double longitude) {
        try {
            log.info("Geocodificación inversa: lat={}, lng={}", latitude, longitude);
            
            LatLng location = new LatLng(latitude, longitude);
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, location).await();
            
            if (results != null && results.length > 0) {
                GeocodingResult result = results[0];
                
                return GeocodingResponseDto.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .formattedAddress(result.formattedAddress)
                        .placeId(result.placeId)
                        .build();
            }
            
            throw new RuntimeException("No se encontró dirección para las coordenadas");
            
        } catch (Exception e) {
            log.error("Error en geocodificación inversa: {}", e.getMessage());
            throw new RuntimeException("Error en geocodificación inversa: " + e.getMessage());
        }
    }

    /**
     * Calcular distancia y tiempo entre dos puntos
     */
    public DistanceMatrixResponseDto calculateDistance(
            String originLat, String originLng,
            String destLat, String destLng) {
        
        try {
            log.info("Calculando distancia: origen=({},{}) destino=({},{})", 
                    originLat, originLng, destLat, destLng);
            
            String[] origins = new String[]{originLat + "," + originLng};
            String[] destinations = new String[]{destLat + "," + destLng};
            
            DistanceMatrix matrix = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(origins)
                    .destinations(destinations)
                    .mode(TravelMode.DRIVING)
                    .language("es")
                    .await();
            
            if (matrix.rows != null && matrix.rows.length > 0) {
                DistanceMatrixElement element = matrix.rows[0].elements[0];
                
                if (element.status == DistanceMatrixElementStatus.OK) {
                    return DistanceMatrixResponseDto.builder()
                            .origin(matrix.originAddresses[0])
                            .destination(matrix.destinationAddresses[0])
                            .distance(element.distance.humanReadable)
                            .duration(element.duration.humanReadable)
                            .distanceValue(element.distance.inMeters)
                            .durationValue(element.duration.inSeconds)
                            .build();
                }
            }
            
            throw new RuntimeException("No se pudo calcular la distancia");
            
        } catch (Exception e) {
            log.error("Error al calcular distancia: {}", e.getMessage());
            throw new RuntimeException("Error al calcular distancia: " + e.getMessage());
        }
    }

    /**
     * Encontrar el restaurante más cercano a un cliente
     */
    public DistanceMatrixResponseDto findNearestRestaurant(
            String clientLat, String clientLng,
            java.util.List<String> restaurantCoordinates) {
        
        try {
            log.info("Buscando restaurante más cercano para cliente: ({},{})", clientLat, clientLng);
            
            String[] origins = new String[]{clientLat + "," + clientLng};
            String[] destinations = restaurantCoordinates.toArray(new String[0]);
            
            DistanceMatrix matrix = DistanceMatrixApi.newRequest(geoApiContext)
                    .origins(origins)
                    .destinations(destinations)
                    .mode(TravelMode.DRIVING)
                    .language("es")
                    .await();
            
            // Encontrar el restaurante con menor distancia
            long minDistance = Long.MAX_VALUE;
            DistanceMatrixElement nearestElement = null;
            int nearestIndex = -1;
            
            for (int i = 0; i < matrix.rows[0].elements.length; i++) {
                DistanceMatrixElement element = matrix.rows[0].elements[i];
                if (element.status == DistanceMatrixElementStatus.OK) {
                    if (element.distance.inMeters < minDistance) {
                        minDistance = element.distance.inMeters;
                        nearestElement = element;
                        nearestIndex = i;
                    }
                }
            }
            
            if (nearestElement != null) {
                return DistanceMatrixResponseDto.builder()
                        .origin(matrix.originAddresses[0])
                        .destination(matrix.destinationAddresses[nearestIndex])
                        .distance(nearestElement.distance.humanReadable)
                        .duration(nearestElement.duration.humanReadable)
                        .distanceValue(nearestElement.distance.inMeters)
                        .durationValue(nearestElement.duration.inSeconds)
                        .build();
            }
            
            throw new RuntimeException("No se encontró restaurante cercano");
            
        } catch (Exception e) {
            log.error("Error al buscar restaurante cercano: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
