package com.ufps.Quick_Delivery.controllers;

import com.ufps.Quick_Delivery.Client.*;
import com.ufps.Quick_Delivery.dto.ClienteContactoResponse;
import com.ufps.Quick_Delivery.dto.DireccionResponse;
import com.ufps.Quick_Delivery.dto.PedidoResponse;
import com.ufps.Quick_Delivery.dto.ProductoResponse;
import com.ufps.Quick_Delivery.dto.DeliveryUserDto;
import com.ufps.Quick_Delivery.dto.PedidoCompletoResponse;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.repository.EntregaRepository;
import com.ufps.Quick_Delivery.services.DeliveryUserService;
import com.ufps.Quick_Delivery.services.TrackingService;

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
public class DeliveryUserController {

    private final DeliveryUserService service;
    private final ClienteClient clienteClient;
    private final RestauranteClient restauranteClient;
    private final EdgeClient edgeClient;
    private final EntregaRepository entregaRepository;
    private final TrackingService trackingService;

    // ----------------- CRUD BÁSICO -----------------

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

    // ----------------- CLIENTE -----------------

    @GetMapping("/{clienteId}/contacto")
    public ResponseEntity<ClienteContactoResponse> obtenerContactoCliente(
            @PathVariable("clienteId") UUID clienteId) {
        try {
            return ResponseEntity.ok(clienteClient.obtenerContactoCliente(clienteId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ----------------- PEDIDOS -----------------

    @GetMapping("cliente/pedido/{pedidoId}")
    public ResponseEntity<PedidoResponse> obtenerPedido(@PathVariable("pedidoId") UUID pedidoId) {
        PedidoResponse pedido = clienteClient.obtenerPedidoPorId(pedidoId);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pedido);
    }

    // ----------------- DIRECCIONES -----------------

    @GetMapping("/direccion/{direccionId}")
    public ResponseEntity<DireccionResponse> obtenerDireccion(@PathVariable("direccionId") UUID direccionId) {
        try {
            DireccionResponse direccion = edgeClient.obtenerDireccionPorId(direccionId);
            if (direccion == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(direccion);
        } catch (Exception e) {
            return ResponseEntity.status(502).build();
        }
    }

    // ----------------- PRODUCTOS -----------------

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ProductoResponse> obtenerProducto(@PathVariable("productoId") UUID productoId) {
        try {
            ProductoResponse producto = restauranteClient.obtenerProductoPorId(productoId);
            if (producto == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(502).build();
        }
    }

    // ----------------- PEDIDO COMPLETO -----------------

    @GetMapping("/pedido/completo/{pedidoId}")
    public ResponseEntity<PedidoCompletoResponse> obtenerPedidoCompleto(@PathVariable("pedidoId") UUID pedidoId) {

        try {
            PedidoResponse pedido = clienteClient.obtenerPedidoPorId(pedidoId);
            if (pedido == null) return ResponseEntity.notFound().build();

            ClienteContactoResponse cliente =
                    clienteClient.obtenerContactoCliente(pedido.getClienteId());

            DireccionResponse direccion =
                    edgeClient.obtenerDireccionPorId(pedido.getDireccionEntregaId());

            List<ProductoResponse> productos =
                    pedido.getItems().stream()
                            .map(item -> restauranteClient.obtenerProductoPorId(item.getProductoId()))
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

    // ----------------- HISTORIAL DE ENTREGAS -----------------

    @GetMapping("/historial")
    public ResponseEntity<List<Map<String, Object>>> obtenerHistorialEntregas(
            @RequestParam("usuarioId") UUID usuarioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            UUID deliveryId = service.findDeliveryIdByUsuarioId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

            List<Entrega> entregas = entregaRepository.findByRepartidorId(deliveryId);

            List<Map<String, Object>> historial = entregas.stream()
                    .map(entrega -> {
                        Map<String, Object> info = new HashMap<>();
                        try {
                            PedidoResponse pedido = clienteClient
                                    .obtenerPedidoPorId(entrega.getPedidoId());

                            info.put("id", entrega.getId());
                            info.put("pedidoId", entrega.getPedidoId());
                            info.put("estado", entrega.getEstado());
                            info.put("codigoEntrega", entrega.getCodigoConfirmacion());
                            info.put("comentario", entrega.getComentariosEntrega());
                            info.put("fechaCreacion", entrega.getHoraInicio());
                            info.put("fechaActualizacion", entrega.getHoraFin());
                            info.put("duracionMinutos", entrega.getDuracionMinutos());

                            if (pedido != null) {
                                info.put("clienteId", pedido.getClienteId());
                                info.put("restauranteId", pedido.getRestauranteId());
                                info.put("total", pedido.getTotal());
                                info.put("metodoPago", pedido.getMetodoPago());
                                info.put("preferencias", pedido.getPreferencias());
                                info.put("direccionEntregaId", pedido.getDireccionEntregaId());
                            }

                        } catch (Exception ignored) {}

                        return info;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(historial);

        } catch (Exception e) {
            return ResponseEntity.status(502).build();
        }
    }

    // ----------------- UBICACIÓN -----------------

    @PostMapping("/{id}/ubicacion")
    public ResponseEntity<Void> actualizarUbicacion(
            @PathVariable("id") UUID id,
            @RequestParam("latitud") double latitud,
            @RequestParam("longitud") double longitud) {

        boolean updated = service.actualizarUbicacion(id, latitud, longitud);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

}
