package com.ufps.Quick_Delivery.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.ufps.Quick_Delivery.dto.PedidoDto;

@FeignClient(name = "pedido-service", url = "${cliente-service.url}")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos")
    List<PedidoDto> obtenerPedidos();
}
