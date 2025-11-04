package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class GeocodingRequestDto {
    private String calle;
    private String ciudad;
    private String barrio;
    
    // Método helper para construir dirección completa
    public String getFullAddress() {
        return String.format("%s, %s, %s", calle, barrio, ciudad);
    }
}
