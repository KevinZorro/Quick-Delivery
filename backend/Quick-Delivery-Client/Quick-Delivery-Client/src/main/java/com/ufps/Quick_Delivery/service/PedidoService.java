package com.ufps.Quick_Delivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufps.Quick_Delivery.client.ProductoClient;
import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.ItemPedidoDto;
import com.ufps.Quick_Delivery.dto.PedidoDto;
import com.ufps.Quick_Delivery.mapper.PedidoMapper;
import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.ItemPedido;
import com.ufps.Quick_Delivery.model.MetodoPago;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import com.ufps.Quick_Delivery.repository.PedidoRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoClient productoClient;
    private final NotificacionService notificacionService;

    // -------------------------------------------------------------------------
    // CREAR PEDIDO DESDE CARRITO
    // -------------------------------------------------------------------------
    @Transactional
    public Pedido crearPedidoDesdeCarrito(CrearPedidoRequestDto request) {

        System.out.println("🔍 Iniciando creación de pedido...");

        // 1. Buscar cliente
        Cliente cliente = clienteRepository.findByUsuarioId(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + request.getClienteId()));

        // 2. Crear pedido base
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestauranteId(request.getRestauranteId());
        pedido.setDireccionEntregaId(request.getDireccionEntregaId());
        pedido.setPreferencias(request.getPreferencias());
        pedido.setEstado(EstadoPedido.INICIADO);

        // Método de pago
        if (request.getMetodoPago() != null && !request.getMetodoPago().isEmpty()) {
            try {
                pedido.setMetodoPago(MetodoPago.valueOf(request.getMetodoPago().toUpperCase()));
            } catch (Exception e) {
                throw new RuntimeException("Método de pago inválido");
            }
        }

        // 3. Procesar items
        int totalPedido = 0;

        for (ItemPedidoDto itemDto : request.getItems()) {

            System.out.println("🔍 Consultando producto: " + itemDto.getProductoId());
            ProductoClient.ProductoResponse producto = productoClient.obtenerProducto(itemDto.getProductoId());

            if (producto == null || producto.getPrecio() == null) {
                throw new RuntimeException("Producto no encontrado o sin precio: " + itemDto.getProductoId());
            }

            System.out.println("✅ Producto encontrado: " + producto.getNombre() +
                    " - Precio: $" + producto.getPrecio());

            if (Boolean.FALSE.equals(producto.getDisponible())) {
                throw new RuntimeException("Producto no disponible: " + producto.getNombre());
            }

            ItemPedido item = new ItemPedido();
            item.setProductoId(itemDto.getProductoId());
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnidad(producto.getPrecio());
            item.setSubtotal(producto.getPrecio() * itemDto.getCantidad());

            System.out.println("   📝 Item: " + producto.getNombre()
                    + " x" + itemDto.getCantidad()
                    + " = $" + item.getSubtotal());

            pedido.addItem(item);
            totalPedido += item.getSubtotal();
        }

        pedido.setTotal(totalPedido);

        return pedidoRepository.save(pedido);
    }

    // -------------------------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // ACTUALIZAR ESTADO + NOTIFICACIÓN
    // -------------------------------------------------------------------------
    @Transactional
    public Pedido actualizarEstadoPedido(@NonNull UUID pedidoId, EstadoPedido nuevoEstado) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        System.out.println("📊 Estado del pedido " + pedidoId + " actualizado a: " + nuevoEstado);

        Pedido actualizado = pedidoRepository.save(pedido);

        notificacionService.notificarCambioEstado(actualizado);

        return actualizado;
    }

    // -------------------------------------------------------------------------
    // ACTUALIZAR MÉTODO DE PAGO
    // -------------------------------------------------------------------------
    @Transactional
    public Pedido actualizarMetodoPago(@NonNull UUID pedidoId, MetodoPago metodoPago) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setMetodoPago(metodoPago);

        System.out.println("💳 Método de pago del pedido " + pedidoId + " actualizado a: " + metodoPago);

        return pedidoRepository.save(pedido);
    }

    // -------------------------------------------------------------------------
    // HISTORIAL PARA CLIENTE
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuario(UUID usuarioId) {

        System.out.println("📦 Buscando pedidos del usuario: " + usuarioId);

        List<Pedido> pedidos = pedidoRepository
                .findByCliente_UsuarioIdOrderByFechaCreacionDesc(usuarioId);

        System.out.println("✅ Se encontraron " + pedidos.size() + " pedidos");

        return pedidos;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuarioYEstado(UUID usuarioId, EstadoPedido estado) {

        System.out.println("📦 Buscando pedidos del usuario: " + usuarioId + " con estado: " + estado);

        List<Pedido> pedidos = pedidoRepository
                .findByCliente_UsuarioIdAndEstadoOrderByFechaCreacionDesc(usuarioId, estado);

        System.out.println("✅ Se encontraron " + pedidos.size() + " pedidos");

        return pedidos;
    }

    @Transactional(readOnly = true)
    public long contarPedidosPorUsuario(UUID usuarioId) {

        long count = pedidoRepository.countByCliente_UsuarioId(usuarioId);

        System.out.println("🔢 Total de pedidos del usuario " + usuarioId + ": " + count);

        return count;
    }

    // -------------------------------------------------------------------------
    // HISTORIAL PARA RESTAURANTE
    // -------------------------------------------------------------------------
    public List<PedidoDto> obtenerHistorial(
            UUID restauranteId,
            String fechaInicio,
            String fechaFin,
            String estado,
            UUID clienteId
    ) {

        LocalDateTime inicio = fechaInicio != null ? LocalDateTime.parse(fechaInicio) : null;
        LocalDateTime fin = fechaFin != null ? LocalDateTime.parse(fechaFin) : null;

        EstadoPedido estadoEnum = null;
        if (estado != null && !estado.isBlank()) {
            try {
                estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido: " + estado);
            }
        }

        return pedidoRepository.filtrarPedidos(
                        restauranteId, inicio, fin, estadoEnum, clienteId)
                .stream()
                .map(PedidoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoDto> obtenerHistorialConItems(UUID restauranteId) {

        return pedidoRepository
                .findHistorialByRestauranteIdConItems(restauranteId)
                .stream()
                .map(PedidoMapper::toDto)
                .toList();
    }
}
