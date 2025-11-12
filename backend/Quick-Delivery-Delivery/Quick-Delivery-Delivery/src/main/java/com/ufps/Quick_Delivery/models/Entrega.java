package com.ufps.Quick_Delivery.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID pedidoId;
    @Column(nullable = false)
    private UUID repartidorId;
    @Column(nullable = false)
    private UUID clienteId;
    @Column(nullable = false)
    private String codigoEntrega; 
    @Column(nullable = false)
    private String estado; 
    private String comentarios;
}
