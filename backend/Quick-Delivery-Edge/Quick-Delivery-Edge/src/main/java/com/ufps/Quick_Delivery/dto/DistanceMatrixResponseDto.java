package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanceMatrixResponseDto {
    private String origin;
    private String destination;
    private String distance; // "5.2 km"
    private String duration; // "15 mins"
    private Long distanceValue; // metros
    private Long durationValue; // segundos
}
