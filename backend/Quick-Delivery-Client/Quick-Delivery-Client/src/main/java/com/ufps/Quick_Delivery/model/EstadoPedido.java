package com.ufps.Quick_Delivery.model;

/**
 * Enum que representa los diferentes estados de un pedido.
 */
public enum EstadoPedido {
    INICIADO,
    EN_COCINA,
    CON_EL_REPARTIDOR,
    ENTREGADO,
    RECHAZADO_POR_RESTAURANTE,
    RECHAZADO_POR_REPARTIDOR
}
