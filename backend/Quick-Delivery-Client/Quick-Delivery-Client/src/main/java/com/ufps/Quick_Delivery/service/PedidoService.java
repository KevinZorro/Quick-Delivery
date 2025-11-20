package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.ProductoClient;
import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.ItemPedidoDto;
import com.ufps.Quick_Delivery.dto.PedidoDto;
import com.ufps.Quick_Delivery.mapper.PedidoMapper; // ⭐ Asegúrate que exista este mapper
import com.ufps.Quick_Delivery.model.*;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import com.ufps.Quick_Delivery.repository.PedidoRepository;
import com.ufps.Quick_Delivery.service.NotificacionService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;  // ⭐ IMPORT NECESARIO
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoClient productoClient; 
    private final NotificacionService notificacionService;

    @Transactional
    public Pedido crearPedidoDesdeCarrito(CrearPedidoRequestDto request) {

        // 1. Buscar el cliente
        Cliente cliente = clienteRepository.findByUsuarioId(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + request.getClienteId()));

        // 2. Crear pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestauranteId(request.getRestauranteId());
        pedido.setDireccionEntregaId(request.getDireccionEntregaId());
        pedido.setPreferencias(request.getPreferencias());
        pedido.setEstado(EstadoPedido.INICIADO);

        // Método de pago
        if (request.getMetodoPago() != null) {
            try {
                pedido.setMetodoPago(MetodoPago.valueOf(request.getMetodoPago().toUpperCase()));
            } catch (Exception e) {
                throw new RuntimeException("Método de pago inválido");
            }
        }

        // 3. Items
        int totalPedido = 0;

        for (ItemPedidoDto itemDto : request.getItems()) {

            ProductoClient.ProductoResponse producto =
                    productoClient.obtenerProducto(itemDto.getProductoId());

            if (producto == null || producto.getPrecio() == null) {
                throw new RuntimeException("Producto no encontrado: " + itemDto.getProductoId());
            }

            if (Boolean.FALSE.equals(producto.getDisponible())) {
                throw new RuntimeException("Producto no disponible: " + producto.getNombre());
            }

            ItemPedido item = new ItemPedido();
            item.setProductoId(itemDto.getProductoId());
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnidad(producto.getPrecio());
            item.setSubtotal(producto.getPrecio() * itemDto.getCantidad());

            pedido.addItem(item);
            totalPedido += item.getSubtotal();
        }

        pedido.setTotal(totalPedido);

        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> buscarPorId(@NonNull UUID id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public void eliminarPorId(@NonNull UUID id) {
        pedidoRepository.deleteById(id);
    }

    public Pedido guardarPedido(@NonNull Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarEstadoPedido(@NonNull UUID pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);

        // llamar al servicio de notificaciones (ahora inyectado)
        notificacionService.notificarCambioEstado(actualizado);
        return actualizado;
    }



    @Transactional
    public Pedido actualizarMetodoPago(@NonNull UUID pedidoId, MetodoPago metodoPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setMetodoPago(metodoPago);
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuario(UUID usuarioId) {
        return pedidoRepository.findByCliente_UsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuarioYEstado(UUID usuarioId, EstadoPedido estado) {
        return pedidoRepository.findByCliente_UsuarioIdAndEstadoOrderByFechaCreacionDesc(usuarioId, estado);
    }

    @Transactional(readOnly = true)
    public long contarPedidosPorUsuario(UUID usuarioId) {
        return pedidoRepository.countByCliente_UsuarioId(usuarioId);
    }

    // ⭐ HISTORIAL PARA EL RESTAURANTE
    public List<PedidoDto> obtenerHistorial(
        UUID restauranteId,
        String fechaInicio,
        String fechaFin,
        String estado,
        UUID clienteId
) {
    LocalDateTime inicio = fechaInicio != null ? LocalDateTime.parse(fechaInicio) : null;
    LocalDateTime fin = fechaFin != null ? LocalDateTime.parse(fechaFin) : null;

    // ⭐ Convertir el String a Enum
    EstadoPedido estadoEnum = null;
    if (estado != null && !estado.isBlank()) {
        try {
            estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + estado);
        }
    }

    return pedidoRepository.filtrarPedidos(
            restauranteId, inicio, fin, estadoEnum, clienteId
    ).stream()
    .map(PedidoMapper::toDto)
    .toList();
    }
}

