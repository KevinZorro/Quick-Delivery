// src/main/java/com/ufps/Quick_Delivery/client/dto/PedidoResponse.java
package com.ufps.Quick_Delivery.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoResponse {
    private UUID id;
    private UUID clienteId;
    private UUID restauranteId;
    private UUID repartidorId;
    private UUID direccionEntregaId;
    private Integer total;
    private String estado;
    private String metodoPago;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String preferencias;
    private List<ItemPedidoResponse> items;
}
