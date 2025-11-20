package com.ufps.Quick_Delivery.client;

import lombok.Data;

@Data
public class DistanceMatrixRequest {
    private String originLat;
    private String originLng;
    private String destinationLat;
    private String destinationLng;
}

