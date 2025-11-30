package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.dto.AsignarRepartidorResponse;
import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.client.DeliveryFeignClient;
import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.MetodoPago;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Validated
public class PedidoController {

    private final PedidoService pedidoService;
    private final DeliveryFeignClient deliveryClient;

    @PostMapping("/crear-desde-carrito")
    public ResponseEntity<?> crearPedidoDesdeCarrito(
            @Valid @RequestBody CrearPedidoRequestDto request) {

        try {
            // Log detallado
            System.out.println("═══════════════════════════════════════");
            System.out.println("📦 CREANDO PEDIDO");
            System.out.println("═══════════════════════════════════════");
            System.out.println("🆔 Cliente ID: " + request.getClienteId());
            System.out.println("🍽️ Restaurante ID: " + request.getRestauranteId());
            System.out.println("💳 Método de pago: " + request.getMetodoPago());
            System.out.println("📝 Cantidad de items: " + request.getItems().size());
            System.out.println("📍 Total: " + request.getTotal());
            System.out.println("═══════════════════════════════════════");

            // Validaciones
            if (request.getRestauranteId() == null) {
                return ResponseEntity.badRequest()
                        .body("El ID del restaurante es requerido");
            }

            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Debe haber al menos un item en el pedido");
            }

            // Crear el pedido
            Pedido pedidoCreado = pedidoService.crearPedidoDesdeCarrito(request);
            System.out.println("✅ Pedido creado exitosamente!");
            System.out.println("🆔 ID del pedido: " + pedidoCreado.getId());
            System.out.println("💰 Total: " + pedidoCreado.getTotal());
            System.out.println("═══════════════════════════════════════");

            return ResponseEntity.ok(pedidoCreado);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("❌ Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable("id") UUID id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable("id") UUID id) {
        pedidoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable("id") UUID id, @RequestParam EstadoPedido estado) {
        try {
            Pedido actualizado = pedidoService.actualizarEstadoPedido(id, estado);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error al cambiar estado: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/metodopago")
    public ResponseEntity<?> cambiarMetodoPago(@PathVariable("id") UUID id, @RequestParam MetodoPago metodoPago) {
        try {
            Pedido actualizado = pedidoService.actualizarMetodoPago(id, metodoPago);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error al cambiar método de pago: " + e.getMessage());
        }
    }

    /**
     * Listar pedidos de un usuario
     * GET /api/pedidos/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> listarPedidosUsuario(@PathVariable("usuarioId") UUID usuarioId) {
        System.out.println("🔍 Endpoint: Listar pedidos del usuario: " + usuarioId);
        List<Pedido> pedidos = pedidoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Listar pedidos de un usuario por estado
     * GET /api/pedidos/usuario/{usuarioId}/estado/{estado}
     */
    @GetMapping("/usuario/{usuarioId}/estado/{estado}")
    public ResponseEntity<List<Pedido>> listarPedidosUsuarioPorEstado(
            @PathVariable("usuarioId") UUID usuarioId,
            @PathVariable("estado") EstadoPedido estado) {

        System.out.println("🔍 Endpoint: Listar pedidos del usuario: " + usuarioId + " con estado: " + estado);
        List<Pedido> pedidos = pedidoService.listarPorUsuarioYEstado(usuarioId, estado);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Contar pedidos de un usuario
     * GET /api/pedidos/usuario/{usuarioId}/count
     */
    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarPedidosUsuario(@PathVariable("usuarioId") UUID usuarioId) {
        System.out.println("🔢 Endpoint: Contar pedidos del usuario: " + usuarioId);
        long count = pedidoService.contarPedidosPorUsuario(usuarioId);
        return ResponseEntity.ok(count);
    }

    // hu21
    // HU021 - Listar pedidos por repartidor
    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<Pedido>> obtenerPorRepartidor(@PathVariable UUID repartidorId) {
        System.out.println("🔍 Endpoint: Listar pedidos del repartidor: " + repartidorId);
        return ResponseEntity.ok(pedidoService.findByRepartidorId(repartidorId));
    }

    /**
     * Asignar repartidor a un pedido
     * PATCH /api/pedidos/{id}/repartidor?repartidorId={repartidorId}
     */
    @PatchMapping("/{id}/repartidor")
    public ResponseEntity<?> asignarRepartidor(
            @PathVariable("id") UUID id,
            @RequestParam("repartidorId") UUID repartidorId) {
        try {
            // lógica de negocio existente en el servicio
            Pedido actualizado = pedidoService.asignarRepartidor(id, repartidorId);

            // llamada a Delivery para iniciar la entrega y obtener el código
            DeliveryFeignClient.IniciarEntregaRequest req = new DeliveryFeignClient.IniciarEntregaRequest();
            req.setPedidoId(id);
            req.setRepartidorId(repartidorId);

            DeliveryFeignClient.EntregaResponse entrega = deliveryClient.iniciarEntrega(req);

            AsignarRepartidorResponse resp =
                    new AsignarRepartidorResponse(actualizado, entrega.getCodigoConfirmacion());

            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error al asignar repartidor: " + e.getMessage());
        }
    }

    
}
