package com.ufps.Quick_Delivery.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "interrupcion_especial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterrupcionEspecial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private LocalDate fecha;

    private String motivo;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;
}