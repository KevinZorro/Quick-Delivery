package com.ufps.Quick_Delivery.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ufps.Quick_Delivery.dto.PedidoDto;

@FeignClient(name = "pedido-service", url = "${pedido-service.url}")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos")
    List<PedidoDto> obtenerPedidos();

    @GetMapping("/api/pedidos/restaurante/{restauranteId}/historial-completo")
    List<PedidoDto> obtenerHistorialCompleto(@PathVariable("restauranteId") UUID restauranteId);
}



