package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.PedidoCliente;
import com.ufps.Quick_Delivery.repository.PedidoClienteRepository;


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
    public Optional<PedidoCliente> getPedidoById(Long id) {
        return pedidoClienteRepository.findById(id);
    }

    // Crear nuevo pedido
    public PedidoCliente createPedido(PedidoCliente pedidoCliente) {
        // Aquí puedes agregar validación de negocio si lo necesitas
        return pedidoClienteRepository.save(pedidoCliente);
    }

    // Actualizar pedido
 public PedidoCliente updatePedido(Long id, PedidoCliente pedidoActualizado) {
        return pedidoClienteRepository.findById(id).map(pedido -> {
            pedido.setClienteId(pedidoActualizado.getClienteId());
            pedido.setRestauranteId(pedidoActualizado.getRestauranteId());
            pedido.setFechaPedido(pedidoActualizado.getFechaPedido());
            pedido.setFechaHoraEstimada(pedidoActualizado.getFechaHoraEstimada());
            pedido.setMetodoPago(pedidoActualizado.getMetodoPago());
            pedido.setTiempoEstimado(pedidoActualizado.getTiempoEstimado());
            pedido.setTotal(pedidoActualizado.getTotal());
            pedido.setEstado(pedidoActualizado.getEstado());
            pedido.setInstrucciones(pedidoActualizado.getInstrucciones());
            pedido.setDireccionEntregaId(pedidoActualizado.getDireccionEntregaId());
            pedido.setDireccionEntregaId(pedidoActualizado.getDireccionEntregaId());

            return pedidoClienteRepository.save(pedido);
        }).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    // Eliminar pedido
    public void deletePedido(Long id) {
        if (!pedidoClienteRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado");
        }
        pedidoClienteRepository.deleteById(id);
    }

}
