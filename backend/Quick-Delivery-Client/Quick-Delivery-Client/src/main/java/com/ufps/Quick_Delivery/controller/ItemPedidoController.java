package com.ufps.Quick_Delivery.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufps.Quick_Delivery.dto.CarritoRequest;
import com.ufps.Quick_Delivery.model.ItemPedido;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.service.ItemPedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/itemspedido")
@RequiredArgsConstructor
@Validated
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    // Crear item individual
    @PostMapping
    public ResponseEntity<ItemPedido> crearItem(@Valid @RequestBody ItemPedido itemPedido) {
        ItemPedido creado = itemPedidoService.guardarItem(itemPedido);
        return ResponseEntity.ok(creado);
    }

    // Obtener item por id
    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> obtenerItem(@PathVariable UUID id) {
        return itemPedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar items por pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<ItemPedido>> listarItemsPorPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(itemPedidoService.listarPorPedido(pedidoId));
    }

    // Listar todos los items
    @GetMapping
    public ResponseEntity<List<ItemPedido>> listarTodos() {
        return ResponseEntity.ok(itemPedidoService.listarTodos());
    }

    // Eliminar item por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable UUID id) {
        itemPedidoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Agregar productos al carrito
    @PostMapping("/carrito")
    public ResponseEntity<Pedido> agregarAlCarrito(@RequestBody CarritoRequest carritoRequest) {

        Pedido pedidoActualizado = itemPedidoService.agregarProductosAlCarrito(
                carritoRequest.getCliente(),
                carritoRequest.getRestauranteId(),
                carritoRequest.getItems()
        );

        return ResponseEntity.ok(pedidoActualizado);
    }
}
