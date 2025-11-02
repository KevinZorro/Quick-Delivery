package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.ItemPedidoDto;
import com.ufps.Quick_Delivery.model.*;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import com.ufps.Quick_Delivery.repository.ItemPedidoRepository;
import com.ufps.Quick_Delivery.repository.PedidoRepository;
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
    private final ItemPedidoRepository itemPedidoRepository;
    private final ClienteRepository clienteRepository;
    // Si tienes un servicio o Feign Client para obtener productos del microservicio de restaurantes:
    // private final ProductoService productoService;

    @Transactional
    public Pedido crearPedidoDesdeCarrito(CrearPedidoRequestDto request) {
        // 1. Buscar el cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Crear el pedido (sin ID, será generado automáticamente)
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestauranteId(request.getRestauranteId());
        pedido.setDireccionEntregaId(request.getDireccionEntregaId());
        pedido.setPreferencias(request.getPreferencias());
        pedido.setEstado(EstadoPedido.INICIADO); // Estado inicial

        // 3. Calcular el total y crear los items
        int totalPedido = 0;

        for (ItemPedidoDto itemDto : request.getItems()) {
            // Aquí deberías obtener el precio del producto desde el microservicio de restaurantes
            // Por ahora, asumimos que el precio viene en itemDto o lo calculas
            // Ejemplo: ProductoDto producto = productoService.obtenerProducto(itemDto.getProductoId());
            
            // Por simplicidad, voy a asumir que tienes el precio disponible
            // Si no, debes hacer una llamada HTTP/Feign al microservicio de productos
            int precioUnidad = obtenerPrecioProducto(itemDto.getProductoId());
            
            ItemPedido item = new ItemPedido();
            item.setProductoId(itemDto.getProductoId());
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnidad(precioUnidad);
            item.setSubtotal(precioUnidad * itemDto.getCantidad());
            
            pedido.addItem(item); // Esto agrega el item y establece la relación bidireccional
            
            totalPedido += item.getSubtotal();
        }

        pedido.setTotal(totalPedido);

        // 4. Guardar el pedido (esto guardará también los items por el cascade)
        return pedidoRepository.save(pedido);
    }

    // Método auxiliar para obtener el precio del producto
    // Implementa esto según tu arquitectura (REST Template, Feign, etc.)
    private int obtenerPrecioProducto(UUID productoId) {
        // TODO: Llamar al microservicio de productos/restaurantes
        // Por ahora retorna un valor de ejemplo
        return 10000; // Precio en centavos o pesos
    }

    public Optional<Pedido> buscarPorId(UUID id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public void eliminarPorId(UUID id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public Pedido actualizarEstadoPedido(UUID id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarMetodoPago(UUID id, MetodoPago metodoPago) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setMetodoPago(metodoPago);
        return pedidoRepository.save(pedido);
    }

    // Si necesitas mantener el método antiguo para compatibilidad
    @Deprecated
    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
}
