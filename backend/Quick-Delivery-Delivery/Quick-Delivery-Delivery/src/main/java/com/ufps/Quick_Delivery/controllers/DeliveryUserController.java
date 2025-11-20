package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.client.ClienteClient;
import com.ufps.Quick_Delivery.client.ClienteDireccion;
import com.ufps.Quick_Delivery.client.ClientePedido;
import com.ufps.Quick_Delivery.client.ClienteProducto;
import com.ufps.Quick_Delivery.dto.DeliveryUserDto;
import com.ufps.Quick_Delivery.dto.PedidoCompletoResponse;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.repository.EntregaRepository;
import com.ufps.Quick_Delivery.services.DeliveryUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryUserController {

    private final DeliveryUserService service;
    private final ClienteClient clienteClient;
    private final ClientePedido clientePedido;
    private final ClienteDireccion clienteDireccion;
    private final ClienteProducto clienteProducto;
    private final EntregaRepository entregaRepository; // ✅ AGREGADO

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
            return ResponseEntity.status(502).build();
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
            return ResponseEntity.status(502).build();
        }
    }

    @GetMapping("/pedido/completo/{pedidoId}")
    public ResponseEntity<PedidoCompletoResponse> obtenerPedidoCompleto(@PathVariable UUID pedidoId) {
        try {
            ClientePedido.PedidoResponse pedido = clientePedido.obtenerPedidoPorId(pedidoId);
            if (pedido == null)
                return ResponseEntity.notFound().build();

            ClienteClient.ClienteContactoResponse cliente = clienteClient
                    .obtenerContactoCliente(pedido.getCliente().getId());

            ClienteDireccion.DireccionResponse direccion = clienteDireccion
                    .obtenerDireccionPorId(pedido.getDireccionEntregaId());

            List<ClienteProducto.ProductoResponse> productos = pedido.getItems().stream()
                    .map(item -> clienteProducto.obtenerProductoPorId(item.getProductoId()))
                    .toList();

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
     * ✅ HU021: Repartidor consulta historial de entregas - ACTUALIZADO
     * Ahora devuelve las entregas desde la tabla entregas (con estado real)
     */
    @GetMapping("/historial")
    public ResponseEntity<List<Map<String, Object>>> obtenerHistorialEntregas(
            @RequestParam UUID usuarioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            System.out.println("📋 Obteniendo historial para repartidor usuarioId: " + usuarioId);

            // 1. Buscar deliveryId a partir del usuarioId
            UUID deliveryId = service.findDeliveryIdByUsuarioId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado para el usuario"));

            System.out.println("✅ Delivery ID encontrado: " + deliveryId);

            // 2. Obtener ENTREGAS (no pedidos) desde la tabla entregas
            List<Entrega> entregas = entregaRepository.findByRepartidorId(deliveryId);
            
            System.out.println("✅ Entregas encontradas: " + entregas.size());

            // 3. Para cada entrega, obtener información del pedido
            List<Map<String, Object>> historial = entregas.stream()
                    .map(entrega -> {
                        Map<String, Object> info = new HashMap<>();
                        
                        System.out.println("  📦 Procesando entrega ID: " + entrega.getId() + 
                                         " - Estado: " + entrega.getEstado() + 
                                         " - Pedido: " + entrega.getPedidoId());

                        try {
                            // Obtener datos del pedido desde el microservicio
                            ClientePedido.PedidoResponse pedido = clientePedido.obtenerPedidoPorId(entrega.getPedidoId());

                            // ✅ Construir la respuesta con el estado REAL de la tabla entregas
                            info.put("id", entrega.getId());
                            info.put("pedidoId", entrega.getPedidoId());
                            info.put("estado", entrega.getEstado()); // ✅ Estado de la tabla entregas
                            info.put("codigoEntrega", entrega.getCodigoConfirmacion());
                            info.put("comentario", entrega.getComentariosEntrega());
                            info.put("fechaCreacion", entrega.getHoraInicio());
                            info.put("fechaActualizacion", entrega.getHoraFin());
                            info.put("duracionMinutos", entrega.getDuracionMinutos());
                            
                            // Agregar datos del pedido
                            if (pedido != null) {
                                info.put("clienteId", pedido.getCliente().getId());
                                info.put("restauranteId", pedido.getRestauranteId());
                                info.put("total", pedido.getTotal());
                                info.put("metodoPago", pedido.getMetodoPago());
                                info.put("preferencias", pedido.getPreferencias());
                                info.put("direccionEntregaId", pedido.getDireccionEntregaId());
                            }

                        } catch (Exception e) {
                            System.out.println("⚠️ Error al obtener datos del pedido: " + e.getMessage());
                            // Si falla obtener el pedido, al menos devolver datos básicos de la entrega
                            info.put("id", entrega.getId());
                            info.put("pedidoId", entrega.getPedidoId());
                            info.put("estado", entrega.getEstado());
                            info.put("codigoEntrega", entrega.getCodigoConfirmacion());
                            info.put("fechaCreacion", entrega.getHoraInicio());
                            info.put("fechaActualizacion", entrega.getHoraFin());
                        }

                        return info;
                    })
                    .collect(Collectors.toList());

            // 4. Aplicar filtros
            if (estado != null && !estado.isEmpty()) {
                historial = historial.stream()
                        .filter(h -> estado.equalsIgnoreCase((String) h.get("estado")))
                        .collect(Collectors.toList());
            }

            if (fechaInicio != null) {
                historial = historial.stream()
                        .filter(h -> {
                            Object fecha = h.get("fechaCreacion");
                            if (fecha instanceof java.time.LocalDateTime) {
                                LocalDate fechaEntrega = ((java.time.LocalDateTime) fecha).toLocalDate();
                                return fechaEntrega.isEqual(fechaInicio) || fechaEntrega.isAfter(fechaInicio);
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            }

            if (fechaFin != null) {
                historial = historial.stream()
                        .filter(h -> {
                            Object fecha = h.get("fechaCreacion");
                            if (fecha instanceof java.time.LocalDateTime) {
                                LocalDate fechaEntrega = ((java.time.LocalDateTime) fecha).toLocalDate();
                                return fechaEntrega.isEqual(fechaFin) || fechaEntrega.isBefore(fechaFin);
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
            }

            System.out.println("✅ Devolviendo " + historial.size() + " entregas después de filtros");

            return ResponseEntity.ok(historial);

        } catch (RuntimeException e) {
            System.out.println("❌ Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.out.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(502).build();
        }
    }
}
