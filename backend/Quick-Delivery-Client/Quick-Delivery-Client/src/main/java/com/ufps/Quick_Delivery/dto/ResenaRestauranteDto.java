package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ResenaRestauranteDto {
    private UUID id;
    private UUID restauranteId;
    private UUID clienteId;
    private UUID pedidoId;
    private int calificacion;        // 1 a 5 estrellas
    private String comentario;
    private LocalDateTime fechaCreacion;
}
