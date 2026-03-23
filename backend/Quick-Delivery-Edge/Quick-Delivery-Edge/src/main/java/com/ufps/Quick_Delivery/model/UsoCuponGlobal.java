package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsoCuponGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID cuponId;

    @Column(nullable = false)
    private UUID clienteId;

    @Column(nullable = false)
    private LocalDateTime fechaUso;

    // Referencia al pedido donde se aplicó el cupón
    private UUID pedidoId;
}
