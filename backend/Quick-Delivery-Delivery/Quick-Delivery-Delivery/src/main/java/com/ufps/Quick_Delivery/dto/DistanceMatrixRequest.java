// src/main/java/com/ufps/Quick_Delivery/client/dto/DistanceMatrixRequest.java
package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class DistanceMatrixRequest {
    // puedes adaptar a lo que espera tu servicio edge/maps; ejemplo:
    private String originLat;
    private String originLng;
    private String destinationLat;
    private String destinationLng;
    // o
    // private List<String> origins;
    // private List<String> destinations;
}
