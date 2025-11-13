package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.*;
import com.ufps.Quick_Delivery.repository.ItemPedidoRepository;
import com.ufps.Quick_Delivery.repository.PedidoRepository;

import lombok.NonNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@Transactional
public class ItemPedidoService {

    private final ItemPedidoRepository itemRepo;
    private final PedidoRepository pedidoRepo;

    public ItemPedidoService(ItemPedidoRepository itemRepo, PedidoRepository pedidoRepo) {
        this.itemRepo = itemRepo;
        this.pedidoRepo = pedidoRepo;
    }

    public ItemPedido guardarItem(@NonNull ItemPedido item) {
        // Guarda un item pedido simple (ejemplo)
        return itemRepo.save(item);
    }

    public Optional<ItemPedido> buscarPorId(@NonNull UUID id) {
        return itemRepo.findById(id);
    }

    public List<ItemPedido> listarPorPedido(UUID pedidoId) {
        return itemRepo.findByPedidoId(pedidoId);
    }

    public List<ItemPedido> listarTodos() {
        return itemRepo.findAll();
    }

    public void eliminarPorId(@NonNull UUID id) {
        itemRepo.deleteById(id);
    }

    // Método para agregar productos al carrito (pedido en estado INICIADO)
public Pedido agregarProductosAlCarrito(Cliente cliente, UUID restauranteId, List<ItemPedido> nuevosItems) {
    // Buscar pedido abierto para ese cliente y restaurante
    Optional<Pedido> pedidoOpt = pedidoRepo.findByClienteAndRestauranteIdAndEstado(cliente, restauranteId, EstadoPedido.INICIADO);

    Pedido pedido = pedidoOpt.orElseGet(() -> {
        // Crear nuevo pedido en estado INICIADO si no existe
        Pedido p = new Pedido();
        p.setCliente(cliente);
        p.setRestauranteId(restauranteId);
        p.setEstado(EstadoPedido.INICIADO);
        p.setTotal(0);
        return pedidoRepo.save(p);
    });

    for (ItemPedido nuevoItem : nuevosItems) {
        Optional<ItemPedido> existenteOpt = itemRepo.findByPedidoIdAndProductoId(pedido.getId(), nuevoItem.getProductoId());
        if (existenteOpt.isPresent()) {
            ItemPedido existente = existenteOpt.get();
            existente.setCantidad((short)(existente.getCantidad() + nuevoItem.getCantidad()));
            existente.setSubtotal(existente.getCantidad() * existente.getPrecioUnidad());
            itemRepo.save(existente);
        } else {
            nuevoItem.setPedido(pedido);
            nuevoItem.setSubtotal(nuevoItem.getCantidad() * nuevoItem.getPrecioUnidad());
            itemRepo.save(nuevoItem);
        }
    }

    int total = itemRepo.findByPedidoId(pedido.getId()).stream()
            .mapToInt(ItemPedido::getSubtotal)
            .sum();
    pedido.setTotal(total);
    return pedidoRepo.save(pedido);
}

}

