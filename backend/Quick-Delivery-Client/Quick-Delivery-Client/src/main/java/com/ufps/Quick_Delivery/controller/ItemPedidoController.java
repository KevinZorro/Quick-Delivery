package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.model.ItemPedido;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.service.ItemPedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ufps.Quick_Delivery.DTO.CarritoRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/itemspedido")
@RequiredArgsConstructor
@Validated
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    @PostMapping
    public ResponseEntity<ItemPedido> crearItem(@Valid @RequestBody ItemPedido itemPedido) {
        ItemPedido creado = itemPedidoService.guardarItem(itemPedido);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> obtenerItem(@PathVariable UUID id) {
        return itemPedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<ItemPedido>> listarItemsPorPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(itemPedidoService.listarPorPedido(pedidoId));
    }

    @GetMapping
    public ResponseEntity<List<ItemPedido>> listarTodos() {
        return ResponseEntity.ok(itemPedidoService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable UUID id) {
        itemPedidoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

@PostMapping("/carrito")
public ResponseEntity<Pedido> agregarAlCarrito(
        @RequestBody CarritoRequest carritoRequest) {
    
    Pedido pedidoActualizado = itemPedidoService.agregarProductosAlCarrito(
        carritoRequest.getCliente(),
        carritoRequest.getRestauranteId(),
        carritoRequest.getItems()
    );
    return ResponseEntity.ok(pedidoActualizado);
}


}
