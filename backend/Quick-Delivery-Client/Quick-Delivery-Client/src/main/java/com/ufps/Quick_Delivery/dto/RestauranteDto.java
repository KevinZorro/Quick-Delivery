package com.ufps.Quick_Delivery.dto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RestauranteDto {
    
    private UUID id;

    private String correo;
}
