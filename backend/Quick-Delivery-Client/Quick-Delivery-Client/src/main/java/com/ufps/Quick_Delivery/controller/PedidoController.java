package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.MetodoPago;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Validated
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody Pedido pedido) {
        Pedido creado = pedidoService.guardarPedido(pedido);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable UUID id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable UUID id) {
        pedidoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pedido> cambiarEstado(@PathVariable UUID id, @RequestParam EstadoPedido estado) {
        Pedido actualizado = pedidoService.actualizarEstadoPedido(id, estado);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}/metodopago")
    public ResponseEntity<Pedido> cambiarMetodoPago(@PathVariable UUID id, @RequestParam MetodoPago metodoPago) {
        Pedido actualizado = pedidoService.actualizarMetodoPago(id, metodoPago);
        return ResponseEntity.ok(actualizado);
    }
}
