package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ConfirmarEntregaRequest {
    private UUID pedidoId;
    private String codigoEntrega;
    private String comentarios;
}
