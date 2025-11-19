package com.ufps.Quick_Delivery.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistanceMatrixResponse {
    private String origin;
    private String destination;
    private String distance;
    private String duration;
    private Long distanceValue; // en metros
    private Long durationValue; // en segundos
}

