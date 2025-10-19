package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.MetodoPago;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.repository.PedidoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    // Crear o actualizar pedido, validando la entidad
    public Pedido guardarPedido(@Valid Pedido pedido) {
        if (pedido.getTotal() <= 0) {
            throw new IllegalArgumentException("El total debe ser positivo");
        }
        pedido.setFechaActualizacion(LocalDateTime.now());
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }
        return pedidoRepository.save(pedido);
    }

    // Buscar pedido por ID (solo lectura)
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(@NotNull UUID id) {
        return pedidoRepository.findById(id);
    }

    // Listar todos los pedidos
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // Eliminar pedido por ID
    public void eliminarPorId(@NotNull UUID id) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe pedido con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    // Cambiar estado del pedido
    public Pedido actualizarEstadoPedido(@NotNull UUID id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        pedido.setEstado(nuevoEstado);
        pedido.setFechaActualizacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    // Cambiar método de pago
    public Pedido actualizarMetodoPago(@NotNull UUID id, MetodoPago metodoPago) {
        Pedido pedido = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
        pedido.setMetodoPago(metodoPago);
        pedido.setFechaActualizacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }
}
