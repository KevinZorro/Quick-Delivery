package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.dto.PedidoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "pedido-service", url = "http://localhost:8080")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos")
    List<PedidoDto> obtenerPedidos();
}
