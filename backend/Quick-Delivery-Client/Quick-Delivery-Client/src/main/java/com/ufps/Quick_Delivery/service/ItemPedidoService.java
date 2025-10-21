package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.ProductoClient;
import com.ufps.Quick_Delivery.dto.ProductoDto;
import com.ufps.Quick_Delivery.model.ItemPedido;
import com.ufps.Quick_Delivery.repository.ItemPedidoRepository;
import com.ufps.Quick_Delivery.repository.PedidoRepository;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;  // Repositorio local
    private final ProductoClient productoClient;       // Cliente remoto

    public ItemPedido guardarItem(@Valid ItemPedido itemPedido) {

        // Validar existencia local de Pedido
        UUID pedidoId = itemPedido.getPedido().getId();
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new IllegalArgumentException("Pedido no existe con ID: " + pedidoId);
        }

        // Validar existencia remota de Producto
        try {
            ProductoDto productoDto = productoClient.getProductoById(itemPedido.getProductoId());
            if (productoDto == null) {
                throw new IllegalArgumentException("Producto no existe con ID: " + itemPedido.getProductoId());
            }
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Producto no existe con ID: " + itemPedido.getProductoId());
        }

        // Calcula subtotal si no está calculado
        if (itemPedido.getSubtotal() == 0 && itemPedido.getCantidad() > 0) {
            itemPedido.setSubtotal(itemPedido.getCantidad() * itemPedido.getPrecioUnidad());
        }

        return itemPedidoRepository.save(itemPedido);
    }


    // Buscar por ID (solo lectura)
    @Transactional(readOnly = true)
    public Optional<ItemPedido> buscarPorId(@NotNull UUID id) {
        return itemPedidoRepository.findById(id);
    }

    // Listar todos los items
    @Transactional(readOnly = true)
    public List<ItemPedido> listarTodos() {
        return itemPedidoRepository.findAll();
    }

    // Eliminar ItemPedido por ID
    public void eliminarPorId(@NotNull UUID id) {
        if (!itemPedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe ItemPedido con ID: " + id);
        }
        itemPedidoRepository.deleteById(id);
    }

    // Listar items por pedido UUID
    @Transactional(readOnly = true)
    public List<ItemPedido> listarPorPedido(@NotNull UUID pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId);
    }
}
