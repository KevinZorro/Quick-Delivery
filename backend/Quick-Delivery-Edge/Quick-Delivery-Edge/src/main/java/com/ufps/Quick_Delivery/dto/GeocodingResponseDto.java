package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeocodingResponseDto {
    private String address;
    private Double latitude;
    private Double longitude;
    private String formattedAddress;
    private String placeId;
}
