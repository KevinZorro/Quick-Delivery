package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.DeliveryClient;
import com.ufps.Quick_Delivery.client.ProductoClient;
import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.ItemPedidoDto;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoClient productoClient; // ⭐ INYECTAR ProductoClient
    private final DeliveryClient deliveryClient; // ⭐ Cliente Feign hacia Delivery

    @Transactional
    public Pedido crearPedidoDesdeCarrito(CrearPedidoRequestDto request) {
        System.out.println("🔍 Iniciando creación de pedido...");

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
        int totalPedido = 0;

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
                totalPedido += item.getSubtotal();

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
        System.out.println("📊 Estado del pedido " + pedidoId + " actualizado a: " + nuevoEstado);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarMetodoPago(@NonNull UUID pedidoId, MetodoPago metodoPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setMetodoPago(metodoPago);
        System.out.println("💳 Método de pago del pedido " + pedidoId + " actualizado a: " + metodoPago);

        return pedidoRepository.save(pedido);
    }

    /**
     * Listar todos los pedidos de un usuario
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuario(UUID usuarioId) {
        System.out.println("📦 Buscando pedidos del usuario: " + usuarioId);

        List<Pedido> pedidos = pedidoRepository.findByCliente_UsuarioIdOrderByFechaCreacionDesc(usuarioId);

        System.out.println("✅ Se encontraron " + pedidos.size() + " pedidos");
        return pedidos;
    }

    /**
     * Listar pedidos de un usuario por estado
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPorUsuarioYEstado(UUID usuarioId, EstadoPedido estado) {
        System.out.println("📦 Buscando pedidos del usuario: " + usuarioId + " con estado: " + estado);

        List<Pedido> pedidos = pedidoRepository.findByCliente_UsuarioIdAndEstadoOrderByFechaCreacionDesc(usuarioId,
                estado);

        System.out.println("✅ Se encontraron " + pedidos.size() + " pedidos");
        return pedidos;
    }

    /**
     * Contar pedidos de un usuario
     */
    @Transactional(readOnly = true)
    public long contarPedidosPorUsuario(UUID usuarioId) {
        long count = pedidoRepository.countByCliente_UsuarioId(usuarioId);
        System.out.println("🔢 Total de pedidos del usuario " + usuarioId + ": " + count);
        return count;
    }

    // hu21
    @Transactional(readOnly = true)
    public List<Pedido> findByRepartidorId(UUID repartidorId) {
        System.out.println("📦 Buscando pedidos del repartidor: " + repartidorId);
        return pedidoRepository.findByRepartidorIdOrderByFechaCreacionDesc(repartidorId);
    }

    /**
     * Asignar repartidor a un pedido
     */
    @Transactional
    public Pedido asignarRepartidor(@NonNull UUID pedidoId, UUID repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getRepartidorId() != null) {
            throw new RuntimeException("El pedido ya está asignado a otro repartidor");
        }

        if (!pedido.getEstado().equals(EstadoPedido.EN_COCINA)) {
            throw new RuntimeException("El pedido debe estar en estado EN_COCINA para asignar repartidor");
        }

        pedido.setRepartidorId(repartidorId);
        pedido.setEstado(EstadoPedido.CON_EL_REPARTIDOR);
        System.out.println("🚚 Repartidor " + repartidorId + " asignado al pedido " + pedidoId);

        DeliveryClient.IniciarEntregaRequest req = new DeliveryClient.IniciarEntregaRequest();
        req.setPedidoId(pedidoId);
        req.setRepartidorId(repartidorId);

        DeliveryClient.EntregaResponse entrega = deliveryClient.iniciarEntrega(req);
        System.out.println("📦 Entrega creada en Delivery. Código: " + entrega.getCodigoConfirmacion());

        return pedidoRepository.save(pedido);
    }
}
