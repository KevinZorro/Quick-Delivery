package com.ufps.Quick_Delivery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column(nullable = false)
    private double total;  // valor total de la venta

    @Column(nullable = false)
    private LocalDateTime fechaPedido; // cuándo se hizo el pedido

    @Column(nullable = false) 
    private String estado; 

    private long tiempoEntrega; // en minutos

    private boolean entregadoATiempo; // true si se entregó dentro del tiempo estimado y false si no
}
