// src/main/java/com/ufps/Quick_Delivery/client/dto/ItemPedidoResponse.java
package com.ufps.Quick_Delivery.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemPedidoResponse {
    private UUID id;
    private UUID productoId;
    private Integer cantidad;
    private Integer precioUnidad;
    private Integer subtotal;
}
