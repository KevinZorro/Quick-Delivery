package com.ufps.Quick_Delivery.DTO;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteDTO {
    private UUID id;
    private String nombre;
    private String direccion;
}