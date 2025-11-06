package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.dto.PedidoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "pedido-service", url = "https://clientes-service-192433559355.southamerica-east1.run.app")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos")
    List<PedidoDto> obtenerPedidos();
}
