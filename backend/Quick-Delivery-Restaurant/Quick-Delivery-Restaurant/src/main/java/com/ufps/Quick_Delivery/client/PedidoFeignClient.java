package com.ufps.Quick_Delivery.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ufps.Quick_Delivery.dto.PedidoDto;

@FeignClient(name = "pedido-service", url = "${cliente-service.url}")
public interface PedidoFeignClient {

    // 👉 Usado por ReporteService (NO LO TOQUES)
    @GetMapping("/api/pedidos")
    List<PedidoDto> obtenerPedidos();

    // 👉 Nuevo método solo para historial
    @GetMapping("/api/pedidos/restaurante/{restauranteId}/historial-completo")
    List<PedidoDto> obtenerHistorialCompleto(@PathVariable("restauranteId") UUID restauranteId);

}


