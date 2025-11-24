package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ConfirmarEntregaDto {
    private UUID pedidoId;
    private String codigoEntrega;
    private String comentarios; // opcional
}
