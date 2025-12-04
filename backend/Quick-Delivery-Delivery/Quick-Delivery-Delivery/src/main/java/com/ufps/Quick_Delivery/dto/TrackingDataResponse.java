package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingDataResponse {
    private double repartidorLat;
    private double repartidorLng;
    private double clienteLat;
    private double clienteLng;
}
