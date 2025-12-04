package com.ufps.Quick_Delivery.client;

import java.util.UUID;
import lombok.Data;

@Data
public class IniciarEntregaRequest {
    private UUID pedidoId;
    private UUID repartidorId;
}
