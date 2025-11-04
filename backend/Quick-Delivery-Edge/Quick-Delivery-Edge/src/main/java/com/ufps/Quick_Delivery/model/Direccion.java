package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.UUID;

@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "calle", nullable = false, length = 255)
    private String calle;

    @Column(name = "referencia", length = 255)
    private String referencia;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "barrio", nullable = false, length = 100)
    private String barrio;

    @Column(name = "coordenadas", length = 100)
    private String coordenadas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_referencia", nullable = false)
    private TipoReferencia tipoReferencia;
}
