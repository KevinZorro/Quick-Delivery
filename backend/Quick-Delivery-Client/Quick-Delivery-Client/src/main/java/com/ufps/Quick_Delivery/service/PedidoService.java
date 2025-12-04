package com.ufps.Quick_Delivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ufps.Quick_Delivery.client.DeliveryFeignClient;
import com.ufps.Quick_Delivery.client.IniciarEntregaRequest;
import com.ufps.Quick_Delivery.client.ProductoClient;
import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.ItemPedidoDto;
import com.ufps.Quick_Delivery.dto.PedidoDto;
import com.ufps.Quick_Delivery.mapper.PedidoMapper;
import com.ufps.Quick_Delivery.model.*;
import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.ItemPedido;
import com.ufps.Quick_Delivery.model.MetodoPago;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import com.ufps.Quick_Delivery.repository.PedidoRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoClient productoClient; 
    private final DeliveryFeignClient deliveryClient; 
    private final NotificacionService notificacionService;
    // -------------------------------------------------------------------------
    // CREAR PEDIDO DESDE CARRITO

    @Transactional
    public Pedido crearPedidoDesdeCarrito(CrearPedidoRequestDto request) {
        System.out.println("🔍 Iniciando creación de pedido...");
        System.out.println(request.getTotal() + " " + request.getItems().size());

        // 1. Buscar el cliente
        Cliente cliente = clienteRepository.findByUsuarioId(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + request.getClienteId()));

        System.out.println("✅ Cliente encontrado: " + cliente.getId());

        // 2. Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestauranteId(request.getRestauranteId());
        pedido.setDireccionEntregaId(request.getDireccionEntregaId());
        pedido.setPreferencias(request.getPreferencias());
        pedido.setEstado(EstadoPedido.INICIADO);

        // ⭐ ASIGNAR MÉTODO DE PAGO desde el request
        if (request.getMetodoPago() != null && !request.getMetodoPago().isEmpty()) {
            try {
                MetodoPago metodoPago = MetodoPago.valueOf(request.getMetodoPago().toUpperCase());
                pedido.setMetodoPago(metodoPago);
                System.out.println("💳 Método de pago asignado: " + metodoPago);
            } catch (IllegalArgumentException e) {
                System.err.println("⚠️ Método de pago inválido: " + request.getMetodoPago());
                throw new RuntimeException("Método de pago inválido: " + request.getMetodoPago());
            }
        }

        // 3. Calcular el total y crear los items
        int totalPedido = request.getTotal();

        System.out.println("📦 Procesando " + request.getItems().size() + " items...");

        for (ItemPedidoDto itemDto : request.getItems()) {
            try {
                // ⭐ Consultar el producto desde el microservicio de restaurantes
                System.out.println("🔍 Consultando producto: " + itemDto.getProductoId());
                ProductoClient.ProductoResponse producto = productoClient.obtenerProducto(itemDto.getProductoId());

                if (producto == null || producto.getPrecio() == null) {
                    throw new RuntimeException("Producto no encontrado o sin precio: " + itemDto.getProductoId());
                }

                System.out.println(
                        "✅ Producto encontrado: " + producto.getNombre() + " - Precio: $" + producto.getPrecio());

                // Validar que el producto esté disponible
                if (Boolean.FALSE.equals(producto.getDisponible())) {
                    throw new RuntimeException("El producto no está disponible: " + producto.getNombre());
                }

                // Crear el item del pedido
                ItemPedido item = new ItemPedido();
                item.setProductoId(itemDto.getProductoId());
                item.setCantidad(itemDto.getCantidad());
                item.setPrecioUnidad(producto.getPrecio()); // ⭐ Precio real del producto
                item.setSubtotal(producto.getPrecio() * itemDto.getCantidad()); // ⭐ Cálculo correcto

                System.out.println("   📝 Item: " + producto.getNombre() + " x" + itemDto.getCantidad() + " = $"
                        + item.getSubtotal());

                pedido.addItem(item);
                totalPedido = request.getTotal();

            } catch (Exception e) {
                System.err.println("❌ Error al procesar producto " + itemDto.getProductoId() + ": " + e.getMessage());
                throw new RuntimeException("Error al procesar el producto: " + e.getMessage());
            }
        }

        // ⭐ Asignar el total calculado
        pedido.setTotal(totalPedido);

        System.out.println("💰 Total calculado: $" + totalPedido);

        // 4. Guardar el pedido (esto guardará también los items por cascade)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        System.out.println("✅ Pedido guardado con ID: " + pedidoGuardado.getId());

        return pedidoGuardado;
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

        Pedido actualizado = pedidoRepository.save(pedido);

        // Notificación al cliente
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

        return pedidoRepository.save(pedido);
    }

    // -------------------------------------------------------------------------
    // HISTORIAL PARA CLIENTE
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuario(UUID usuarioId) {

        return pedidoRepository
                .findByCliente_UsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuarioYEstado(UUID usuarioId, EstadoPedido estado) {

        return pedidoRepository
                .findByCliente_UsuarioIdAndEstadoOrderByFechaCreacionDesc(usuarioId, estado);
    }

    @Transactional(readOnly = true)
    public long contarPedidosPorUsuario(UUID usuarioId) {
        return pedidoRepository.countByCliente_UsuarioId(usuarioId);
    }

    // -------------------------------------------------------------------------
    // HU-21 — PEDIDOS POR REPARTIDOR
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Pedido> findByRepartidorId(UUID repartidorId) {
        return pedidoRepository.findByRepartidorIdOrderByFechaCreacionDesc(repartidorId);
    }

   @Transactional
    public Pedido asignarRepartidor(@NonNull UUID pedidoId, UUID repartidorId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getRepartidorId() != null) {
            throw new RuntimeException("El pedido ya está asignado a otro repartidor");
        }
        
        if (!pedido.getEstado().equals(EstadoPedido.CON_EL_REPARTIDOR)) {
            throw new RuntimeException("El pedido debe estar en estado CON_EL_REPARTIDOR para asignar repartidor");
        }

        pedido.setRepartidorId(repartidorId);
        System.out.println("🚚 Repartidor " + repartidorId + " asignado al pedido " + pedidoId);
        
        return pedidoRepository.save(pedido);
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
            estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
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
