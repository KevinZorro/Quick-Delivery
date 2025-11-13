package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.models.EstadoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoNotificacionResponseDto {
    
    private UUID id;
    private UUID pedidoId;
    private UUID clienteId;
    private UUID restauranteId;
    private UUID direccionEntregaId;
    private EstadoNotificacion estado;
    private UUID repartidorId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAceptacion;
}

