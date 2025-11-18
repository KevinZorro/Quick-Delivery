package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDisponibleDto {
    private UUID id;
    private UUID restauranteId;
    private UUID direccionEntregaId;
    private Integer total;
    private String estado;
    private LocalDateTime fechaCreacion;
    private String direccionEntrega;
    private String coordenadasEntrega;
    private Double distanciaKm;
    private String distanciaTexto;
    private String tiempoEstimado;
    private List<ItemPedidoDto> items;
}

