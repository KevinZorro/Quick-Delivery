package com.ufps.Quick_Delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "client-service", url = "${client-service.url}/api/pedidos")
public interface PedidoClient {

    @GetMapping
    ResponseEntity<List<PedidoResponse>> listarPedidos();

    @GetMapping("/{id}")
    ResponseEntity<PedidoResponse> obtenerPedido(@PathVariable("id") UUID id);

    @PatchMapping("/{id}/estado")
    ResponseEntity<PedidoResponse> cambiarEstado(
            @PathVariable("id") UUID id,
            @RequestParam("estado") String estado
    );

    @PatchMapping("/{id}/repartidor")
    ResponseEntity<PedidoResponse> asignarRepartidor(
            @PathVariable("id") UUID id,
            @RequestParam("repartidorId") UUID repartidorId
    );
}

