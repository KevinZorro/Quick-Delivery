package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.model.PedidoCliente;
import com.ufps.Quick_Delivery.service.PedidoClienteService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
public class PedidoClienteController {

    @Autowired
    private PedidoClienteService pedidoClienteService;

    // Obtener todos los pedidos
    @GetMapping
    public List<PedidoCliente> getAllPedidos() {
        return pedidoClienteService.getAllPedidos();
    }

    // Obtener un pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoCliente> getPedidoById(@PathVariable Long id) {
        Optional<PedidoCliente> pedido = pedidoClienteService.getPedidoById(id);
        return pedido.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo pedido
    @PostMapping
    public ResponseEntity<PedidoCliente> createPedido(@RequestBody @Valid PedidoCliente pedidoCliente) {
        PedidoCliente creado = pedidoClienteService.createPedido(pedidoCliente);
        return ResponseEntity.ok(creado);
    }

    // Actualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<PedidoCliente> updatePedido(@PathVariable Long id, @RequestBody @Valid PedidoCliente pedidoCliente) {
        try {
            PedidoCliente actualizado = pedidoClienteService.updatePedido(id, pedidoCliente);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        try {
            pedidoClienteService.deletePedido(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
