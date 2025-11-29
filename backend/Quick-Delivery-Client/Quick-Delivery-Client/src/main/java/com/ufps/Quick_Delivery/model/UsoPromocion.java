package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity
public class UsoPromocion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID promocionId;

    @Column(nullable = false)
    private UUID clienteId; // o userId del token

    @Column(nullable = false)
    private LocalDateTime fechaUso;
}
