package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteContactoDto {
    private UUID clienteId;
    private String nombreCompleto;
    private String telefono;
    private String direccionEntrega;
    private String referenciaEntrega;
}
