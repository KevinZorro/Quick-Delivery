package com.ufps.Quick_Delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AceptarPedidoRequestDto {
    
    @NotNull(message = "El ID del repartidor es requerido")
    private UUID repartidorId;
}

