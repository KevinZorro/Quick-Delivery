package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUbicacionResponse {
    private String coordenadas; // "lat,lng" formato que ya usas
}
