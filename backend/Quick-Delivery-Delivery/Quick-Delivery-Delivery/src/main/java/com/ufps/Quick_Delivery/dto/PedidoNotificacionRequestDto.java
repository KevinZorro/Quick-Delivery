package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PedidoNotificacionRequestDto {
    private UUID pedidoId;
    private UUID restauranteId;
    private UUID clienteId;
    private Integer total;
}

