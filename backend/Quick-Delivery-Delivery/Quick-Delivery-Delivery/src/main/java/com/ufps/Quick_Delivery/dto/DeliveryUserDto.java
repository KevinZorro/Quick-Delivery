package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.models.TipoVehiculo;
import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryUserDto {
    private UUID id;
    private UUID usuarioId;
    private TipoVehiculo vehiculo;
    private Double calificacionPromedio;
    private Double ganancias;

}
