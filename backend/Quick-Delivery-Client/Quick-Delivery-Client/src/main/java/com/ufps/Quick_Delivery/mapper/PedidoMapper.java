package com.ufps.Quick_Delivery.mapper;

import com.ufps.Quick_Delivery.dto.PedidoDto;
import com.ufps.Quick_Delivery.model.Pedido;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class PedidoMapper {

    public static PedidoDto toDto(Pedido pedido) {
        if (pedido == null) return null;

        return PedidoDto.builder()
                .id(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .restauranteId(pedido.getRestauranteId())

                .estado(pedido.getEstado().name())
                .total(BigDecimal.valueOf(pedido.getTotal()))
                .fechaCreacion(pedido.getFechaCreacion())
                .preferencias(pedido.getPreferencias())

                // ⭐ AHORA SÍ MAPEAMOS LOS ITEMS
                .items(
                        pedido.getItems().stream()
                            .map(ItemPedidoMapper::toDto)
                            .collect(Collectors.toList())
                )
                .build();
    }
}
