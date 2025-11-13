package com.ufps.Quick_Delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoNotificacionRequestDto {
    
    @NotNull(message = "El ID del pedido es requerido")
    private UUID pedidoId;

    @NotNull(message = "El ID del cliente es requerido")
    private UUID clienteId;

    @NotNull(message = "El ID del restaurante es requerido")
    private UUID restauranteId;

    @NotNull(message = "El ID de la dirección de entrega es requerido")
    private UUID direccionEntregaId;
}

