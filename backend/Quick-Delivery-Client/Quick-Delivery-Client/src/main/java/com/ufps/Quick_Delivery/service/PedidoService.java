package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.RestauranteClient;
import com.ufps.Quick_Delivery.dto.RestauranteDto;
import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.MetodoPago;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.repository.PedidoRepository;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ufps.Quick_Delivery.repository.ClienteRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteClient restauranteClient;

    // Crear o actualizar pedido, validando la entidad
    public Pedido guardarPedido(@Valid Pedido pedido) {
        if (pedido.getTotal() <= 0) {
            throw new IllegalArgumentException("El total debe ser positivo");
        }
        pedido.setFechaActualizacion(LocalDateTime.now());
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }

        // Validar que el restaurante exista y obtener datos
        try {
            RestauranteDto restaurante = restauranteClient.obtenerRestaurantePorId(pedido.getRestauranteId());
            pedido.setRestauranteId(restaurante.getId());
            System.out.println(restaurante.getCorreo());
            // Puedes usar datos de restaurante aquí para lógica adicional
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Restaurante no existe con ID: " + pedido.getRestauranteId());
        }

        Cliente cliente = new Cliente();
        cliente.setUsuarioId(UUID.randomUUID());
        clienteRepository.save(cliente);
        pedido.setCliente(cliente);
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
