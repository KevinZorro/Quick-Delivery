package com.ufps.Quick_Delivery.dto;


import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterrupcionEspecialDto {
    private UUID id;
    private LocalDate fecha;
    private String motivo;
    private UUID restauranteId;
    
}