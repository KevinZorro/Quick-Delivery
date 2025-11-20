package com.ufps.Quick_Delivery.mapper;

import com.ufps.Quick_Delivery.dto.ItemPedidoDto;
import com.ufps.Quick_Delivery.model.ItemPedido;

public class ItemPedidoMapper {

    public static ItemPedidoDto toDto(ItemPedido item) {
        ItemPedidoDto dto = new ItemPedidoDto();
        dto.setProductoId(item.getProductoId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnidad(item.getPrecioUnidad());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
