package com.ufps.Quick_Delivery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

/**
 * DTO para exponer información de contacto del cliente al repartidor
 * Solo contiene datos necesarios para la entrega (protección de datos)
 */
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
    
    // NO exponer: email, contraseña, historial de pedidos, métodos de pago
}
