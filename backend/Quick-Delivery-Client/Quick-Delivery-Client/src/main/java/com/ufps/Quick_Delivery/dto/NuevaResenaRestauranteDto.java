
package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.UUID;

@Data
public class NuevaResenaRestauranteDto {
    private UUID pedidoId;
    private UUID clienteId;
    @Min(1)
    @Max(5)
    private int calificacion;
    private String comentario;
}
