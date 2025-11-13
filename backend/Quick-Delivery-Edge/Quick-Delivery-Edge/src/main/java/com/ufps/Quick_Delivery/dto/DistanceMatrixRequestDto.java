package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class DistanceMatrixRequestDto {
    private String originLat;
    private String originLng;
    private String destinationLat;
    private String destinationLng;
}
