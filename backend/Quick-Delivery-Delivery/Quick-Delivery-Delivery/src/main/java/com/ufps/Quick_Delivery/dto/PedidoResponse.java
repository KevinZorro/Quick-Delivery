// src/main/java/com/ufps/Quick_Delivery/client/dto/PedidoResponse.java
package com.ufps.Quick_Delivery.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    // El Cliente service retorna "cliente" como objeto anidado, no "clienteId"
    @JsonProperty("cliente")
    private void deserializeCliente(Map<String, Object> cliente) {
        if (cliente != null && cliente.get("id") != null) {
            this.clienteId = UUID.fromString(cliente.get("id").toString());
        }
    }
}
