// src/main/java/com/ufps/Quick_Delivery/client/dto/DistanceMatrixResponse.java
package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class DistanceMatrixResponse {
    private String distance;       // texto (ej. "3.2 km")
    private Long distanceValue;    // metros
    private String duration;       // texto (ej. "8 mins")
    private Long durationValue;    // segundos
}
