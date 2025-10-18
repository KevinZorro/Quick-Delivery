package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.PedidoCliente;
import com.ufps.Quick_Delivery.repository.PedidoClienteRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoClienteService {

    @Autowired
    private PedidoClienteRepository pedidoClienteRepository;

    // Obtener todos los pedidos
    public List<PedidoCliente> getAllPedidos() {
        return pedidoClienteRepository.findAll();
    }

    // Obtener pedido por ID
    public Optional<PedidoCliente> getPedidoById(UUID id) {
        return pedidoClienteRepository.findById(id);
    }

    // Crear nuevo pedido
    public PedidoCliente createPedido(PedidoCliente pedidoCliente) {
        if (pedidoCliente.getCliente() == null || pedidoCliente.getProducto() == null) {
        throw new IllegalArgumentException("El pedido debe tener cliente y restaurante asignados");
        }
        return pedidoClienteRepository.save(pedidoCliente);
    }

    // Actualizar pedido
 public PedidoCliente updatePedido(UUID id, PedidoCliente pedidoActualizado) {
        return pedidoClienteRepository.findById(id).map(pedido -> {
            pedido.setFechaPedido(pedidoActualizado.getFechaPedido());
            pedido.setFechaHoraEstimada(pedidoActualizado.getFechaHoraEstimada());
            pedido.setMetodoPago(pedidoActualizado.getMetodoPago());
            pedido.setTiempoEstimado(pedidoActualizado.getTiempoEstimado());
            pedido.setTotal(pedidoActualizado.getTotal());
            pedido.setEstado(pedidoActualizado.getEstado());
            pedido.setInstrucciones(pedidoActualizado.getInstrucciones());
            pedido.setDireccionEntregaId(pedidoActualizado.getDireccionEntregaId());

            return pedidoClienteRepository.save(pedido);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    // Eliminar pedido
    public void deletePedido(UUID id) {
        if (!pedidoClienteRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado");
        }
        pedidoClienteRepository.deleteById(id);
    }

}
