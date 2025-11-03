package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestauranteRequestDto {

    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID usuarioId;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;

    private String imagenUrl;
}
