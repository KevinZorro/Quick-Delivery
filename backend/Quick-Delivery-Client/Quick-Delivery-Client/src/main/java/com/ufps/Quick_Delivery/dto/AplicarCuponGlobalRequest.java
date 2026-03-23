package com.ufps.Quick_Delivery.dto;

import java.util.UUID;

public record AplicarCuponGlobalRequest(
    UUID cuponId,
    UUID clienteId,
    UUID pedidoId
) {}
