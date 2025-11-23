package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilResponseDto {
    private String nombre;
    private String correo;
    private String telefono;
    private String fotoPerfil;
    private Rol rol;
}

