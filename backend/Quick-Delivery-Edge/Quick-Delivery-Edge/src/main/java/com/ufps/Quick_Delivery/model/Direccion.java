package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column(name = "calle",nullable = true, length = 255)
    private String calle;

    @Column(name = "referencia",nullable = true, length = 255)
    private String referencia;

    @Column(name = "ciudad",nullable = true, length = 100)
    private String ciudad;

    @Column(name = "barrio",nullable = true, length = 100)
    private String barrio;

    @Column(name = "coordenadas",nullable = true, length = 100)
    private String coordenadas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_referencia")
    private TipoReferencia tipoReferencia;
}
