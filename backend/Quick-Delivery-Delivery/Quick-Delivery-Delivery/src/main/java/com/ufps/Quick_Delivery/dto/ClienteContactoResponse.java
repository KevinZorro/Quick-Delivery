// src/main/java/com/ufps/Quick_Delivery/client/dto/ClienteContactoResponse.java
package com.ufps.Quick_Delivery.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ClienteContactoResponse {
    private UUID id;
    private String nombre;
    private String telefono;
}
