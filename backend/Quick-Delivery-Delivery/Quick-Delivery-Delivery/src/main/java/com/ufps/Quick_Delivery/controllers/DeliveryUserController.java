package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.client.ClienteClient;
import com.ufps.Quick_Delivery.client.ClienteDireccion;
import com.ufps.Quick_Delivery.client.ClientePedido;
import com.ufps.Quick_Delivery.client.ClienteProducto;
import com.ufps.Quick_Delivery.dto.DeliveryUserDto;
import com.ufps.Quick_Delivery.dto.PedidoCompletoResponse;
import com.ufps.Quick_Delivery.services.DeliveryUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryUserController {

    private final DeliveryUserService service;
    private final ClienteClient clienteClient;
    private final ClientePedido clientePedido;
    private final ClienteDireccion clienteDireccion;
    private final ClienteProducto clienteProducto;

    @GetMapping
    public ResponseEntity<List<DeliveryUserDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryUserDto> getById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DeliveryUserDto> create(@Valid @RequestBody DeliveryUserDto dto) {
        DeliveryUserDto created = service.save(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryUserDto> update(@PathVariable UUID id, @Valid @RequestBody DeliveryUserDto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/ganancias")
    public ResponseEntity<Double> getGanancias(@PathVariable UUID id) {
        return service.findById(id)
                .map(dto -> ResponseEntity.ok(dto.getGanancias()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/ganancias")
    public ResponseEntity<Void> registrarGanancia(@PathVariable UUID id, @RequestParam double valorVenta) {
        service.registrarGanancia(id, valorVenta);
        return ResponseEntity.ok().build();
    }

    /**
     * HU023: Consultar información de contacto del cliente
     * 
     */
    @GetMapping("/{clienteId}/contacto")
    public ResponseEntity<ClienteClient.ClienteContactoResponse> obtenerContactoCliente(
            @PathVariable UUID clienteId) {
        try {
            ClienteClient.ClienteContactoResponse contacto = clienteClient.obtenerContactoCliente(clienteId);
            return ResponseEntity.ok(contacto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("cliente/pedido/{pedidoId}")
    public ResponseEntity<ClientePedido.PedidoResponse> obtenerPedido(@PathVariable UUID pedidoId) {

        ClientePedido.PedidoResponse pedido = clientePedido.obtenerPedidoPorId(pedidoId);

        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/direccion/{direccionId}")
    public ResponseEntity<ClienteDireccion.DireccionResponse> obtenerDireccion(
            @PathVariable UUID direccionId) {

        try {
            ClienteDireccion.DireccionResponse direccion = clienteDireccion.obtenerDireccionPorId(direccionId);

            if (direccion == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(direccion);
        } catch (Exception e) {

            return ResponseEntity.status(502).build(); // BAD_GATEWAY
        }
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ClienteProducto.ProductoResponse> obtenerProducto(
            @PathVariable UUID productoId) {

        try {
            ClienteProducto.ProductoResponse producto = clienteProducto.obtenerProductoPorId(productoId);

            if (producto == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(producto);

        } catch (Exception e) {
            return ResponseEntity.status(502).build(); // BAD_GATEWAY si el otro servicio falla
        }
    }

    @GetMapping("/pedido/completo/{pedidoId}")
    public ResponseEntity<PedidoCompletoResponse> obtenerPedidoCompleto(@PathVariable UUID pedidoId) {
        try {
            // 1. Obtener el pedido
            ClientePedido.PedidoResponse pedido = clientePedido.obtenerPedidoPorId(pedidoId);
            if (pedido == null)
                return ResponseEntity.notFound().build();

            // 2. Obtener datos del cliente
            ClienteClient.ClienteContactoResponse cliente = clienteClient
                    .obtenerContactoCliente(pedido.getCliente().getId());

            // 3. Obtener dirección de entrega
            ClienteDireccion.DireccionResponse direccion = clienteDireccion
                    .obtenerDireccionPorId(pedido.getDireccionEntregaId());

            // 4. Obtener información de los productos del pedido
            List<ClienteProducto.ProductoResponse> productos = pedido.getItems().stream()
                    .map(item -> clienteProducto.obtenerProductoPorId(item.getProductoId()))
                    .toList();

            // 5. Construir respuesta completa
            PedidoCompletoResponse respuesta = new PedidoCompletoResponse();
            respuesta.setPedido(pedido);
            respuesta.setCliente(cliente);
            respuesta.setDireccionEntrega(direccion);
            respuesta.setProductos(productos);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(502).build();
        }
    }

    /**
     * HU021: Repartidor consulta historial de entregas.
     * 
     */
    @GetMapping("/{deliveryId}/historial")
    public ResponseEntity<List<ClientePedido.PedidoResponse>> obtenerHistorialEntregas(
            @PathVariable UUID deliveryId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {

            // 1. Obtener todos los pedidos del repartidor desde el pedido-service
            List<ClientePedido.PedidoResponse> pedidos = clientePedido.obtenerPedidosPorRepartidor(deliveryId);

            // 2. Filtro por estado (si se envía)
            if (estado != null && !estado.isEmpty()) {
                pedidos = pedidos.stream()
                        .filter(p -> p.getEstado().equalsIgnoreCase(estado))
                        .toList();
            }

            // 3. Filtro por fechaInicio
            if (fechaInicio != null) {
                pedidos = pedidos.stream()
                        .filter(p -> p.getFechaCreacion().toLocalDate().isEqual(fechaInicio)
                                || p.getFechaCreacion().toLocalDate().isAfter(fechaInicio))
                        .toList();
            }

            // 4. Filtro por fechaFin
            if (fechaFin != null) {
                pedidos = pedidos.stream()
                        .filter(p -> p.getFechaCreacion().toLocalDate().isEqual(fechaFin)
                                || p.getFechaCreacion().toLocalDate().isBefore(fechaFin))
                        .toList();
            }

            return ResponseEntity.ok(pedidos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(502).build();
        }
    }

    

}
