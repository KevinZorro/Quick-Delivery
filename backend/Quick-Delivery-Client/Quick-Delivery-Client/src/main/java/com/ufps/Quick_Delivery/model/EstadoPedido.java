package com.ufps.Quick_Delivery.model;

/**
 * Enum que representa los diferentes estados de un pedido.
 * NUEVO: Pedido creado, esperando confirmación del restaurante
 * ACEPTADO: Pedido aceptado por el restaurante
 * EN_COCINA: Pedido en preparación
 * CON_EL_REPARTIDOR: Pedido en camino con repartidor
 * ENTREGADO: Pedido entregado al cliente
 * RECHAZADO_POR_RESTAURANTE: Pedido rechazado por el restaurante
 */
public enum EstadoPedido {
    NUEVO,
    INICIADO,
    ACEPTADO,
    EN_COCINA,
    CON_EL_REPARTIDOR,
    ENTREGADO,
    RECHAZADO_POR_RESTAURANTE
}
