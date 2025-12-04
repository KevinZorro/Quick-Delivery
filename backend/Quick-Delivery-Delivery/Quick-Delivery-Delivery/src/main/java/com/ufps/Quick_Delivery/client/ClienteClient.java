package com.ufps.Quick_Delivery.Client;

import com.ufps.Quick_Delivery.dto.ClienteContactoResponse;
import com.ufps.Quick_Delivery.dto.PedidoResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "cliente-service", url = "http://localhost:8080")
public interface ClienteClient {

    @GetMapping("/clientes/{clienteId}/contacto")
    ClienteContactoResponse obtenerContactoCliente(@PathVariable("clienteId") UUID clienteId);

        // Pedidos
    @GetMapping("/api/pedidos")
    ResponseEntity<List<PedidoResponse>> listarPedidos();

    @GetMapping("/api/pedidos/{id}")
    PedidoResponse obtenerPedidoPorId(@PathVariable("id") UUID id);

    @PatchMapping("/api/pedidos/{id}/estado")
    ResponseEntity<PedidoResponse> cambiarEstado(@PathVariable("id") UUID id, @RequestParam("estado") String estado);

    @PutMapping("/api/pedidos/{id}/repartidor")
    ResponseEntity<PedidoResponse> asignarRepartidor(@PathVariable("id") UUID id, @RequestParam("repartidorId") UUID repartidorId);

}
