package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.models.EstadoEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaDto {
    private UUID id;
    private UUID clienteId;
    private String codigoEntrega;
    private String comentario;
    private EstadoEntrega estado;
    private UUID pedidoId;
    private UUID repartidorId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

