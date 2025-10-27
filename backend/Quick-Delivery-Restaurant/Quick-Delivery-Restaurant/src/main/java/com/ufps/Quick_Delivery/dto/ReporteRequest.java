package com.ufps.Quick_Delivery.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReporteRequest {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipoReporte; // "ventas", "entregas", "demoras"
    private String formato;     // "pdf" o "excel"
}
