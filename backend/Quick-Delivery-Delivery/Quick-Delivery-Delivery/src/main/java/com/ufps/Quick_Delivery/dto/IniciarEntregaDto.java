package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class IniciarEntregaDto {
    private UUID pedidoId;
    private UUID repartidorId;
}
