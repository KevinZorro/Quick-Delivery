package com.ufps.Quick_Delivery.dto;

import lombok.Data;

@Data
public class ActualizarFotoPerfilRequest {
    private String fotoPerfil; // Puede ser null para eliminar la foto
}

