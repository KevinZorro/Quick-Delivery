package com.ufps.Quick_Delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private UUID userId;
    
    // Constructor solo con mensaje (para errores)
    public AuthResponse(String message) {
        this.message = message;
        this.userId = null;
    }
}
