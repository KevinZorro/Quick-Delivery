package com.ufps.Quick_Delivery.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequestDto {

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private UUID clienteId;

    @NotNull(message = "El ID del restaurante no puede ser nulo")
    private UUID restauranteId;

    @NotNull(message = "Los items del pedido no pueden ser nulos")
    @NotEmpty(message = "Debe haber al menos un item en el pedido")
    private List<ItemPedidoDto> items;

    private UUID direccionEntregaId;

    private String preferencias;

    // El total se calculará en el backend
    // El metodoPago se actualiza después
    // El estado inicial será INICIADO
}
