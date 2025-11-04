package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class PedidoDto {
    private UUID id;
    private UUID restauranteId;
    private double total;
    private String fechaCreacion;
    private String estado;  // INICIADO, EN_COCINA, COMPLETADO, etc.
    private List<ItemPedidoDto> items;
}
