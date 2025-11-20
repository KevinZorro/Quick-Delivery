package com.ufps.Quick_Delivery.mapper;

import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.dto.PedidoDto;

import java.math.BigDecimal;

public class PedidoMapper {

    public static PedidoDto toDto(Pedido pedido) {
        if (pedido == null) return null;

        return PedidoDto.builder()
                .id(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .restauranteId(pedido.getRestauranteId())

                // Conversión correcta:
                .estado(pedido.getEstado().name()) // enum → String
                .total(BigDecimal.valueOf(pedido.getTotal())) // int → BigDecimal

                .fechaCreacion(pedido.getFechaCreacion())
                .preferencias(pedido.getPreferencias())
                .build();
    }
}
