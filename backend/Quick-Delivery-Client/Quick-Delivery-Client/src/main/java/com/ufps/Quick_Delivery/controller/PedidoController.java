
package com.ufps.Quick_Delivery.controller;


import com.ufps.Quick_Delivery.client.DeliveryFeignClient;
import com.ufps.Quick_Delivery.client.IniciarEntregaRequest;
import com.ufps.Quick_Delivery.dto.AsignarRepartidorResponse;
import com.ufps.Quick_Delivery.dto.CrearPedidoRequestDto;
import com.ufps.Quick_Delivery.dto.PedidoDto;
import com.ufps.Quick_Delivery.mapper.PedidoMapper;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.MetodoPago;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.service.PedidoService;
import com.ufps.Quick_Delivery.client.IniciarEntregaRequest;
import com.ufps.Quick_Delivery.client.EntregaResponse;


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
    

    // --------------------------------------------------------------------
    // CREAR PEDIDO DESDE CARRITO
    // --------------------------------------------------------------------
    @PostMapping("/crear-desde-carrito")
    public ResponseEntity<?> crearPedidoDesdeCarrito(
            @Valid @RequestBody CrearPedidoRequestDto request) {

        try {
            System.out.println("📦 Creando pedido desde carrito");

            if (request.getRestauranteId() == null)
                return ResponseEntity.badRequest().body("El ID del restaurante es requerido");

            if (request.getItems() == null || request.getItems().isEmpty())
                return ResponseEntity.badRequest().body("Debe haber al menos un item en el pedido");

            Pedido pedidoCreado = pedidoService.crearPedidoDesdeCarrito(request);
            return ResponseEntity.ok(pedidoCreado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // CRUD BÁSICO
    // --------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable UUID id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable UUID id) {
        pedidoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // --------------------------------------------------------------------
    // ACTUALIZAR ESTADO Y MÉTODO DE PAGO
    // --------------------------------------------------------------------
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable UUID id, @RequestParam EstadoPedido estado) {
        try {
            return ResponseEntity.ok(pedidoService.actualizarEstadoPedido(id, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al cambiar estado: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/metodopago")
    public ResponseEntity<?> cambiarMetodoPago(@PathVariable UUID id, @RequestParam MetodoPago metodoPago) {
        try {
            return ResponseEntity.ok(pedidoService.actualizarMetodoPago(id, metodoPago));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al cambiar método de pago: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // HISTORIAL / FILTROS / CONSULTAS
    // --------------------------------------------------------------------
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> listarPedidosUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/estado/{estado}")
    public ResponseEntity<List<Pedido>> listarPedidosUsuarioPorEstado(
            @PathVariable UUID usuarioId, @PathVariable EstadoPedido estado) {

        return ResponseEntity.ok(pedidoService.listarPorUsuarioYEstado(usuarioId, estado));
    }

    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarPedidosUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(pedidoService.contarPedidosPorUsuario(usuarioId));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<PedidoDto>> obtenerHistorial(
            @RequestParam(required = false) UUID restauranteId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) UUID clienteId
    ) {
        return ResponseEntity.ok(
                pedidoService.obtenerHistorial(restauranteId, fechaInicio, fechaFin, estado, clienteId)
        );
    }

    @GetMapping("/restaurante/{restauranteId}/historial-completo")
    public List<PedidoDto> historialCompleto(@PathVariable UUID restauranteId) {
        return pedidoService.obtenerHistorialConItems(restauranteId);
    }

    // --------------------------------------------------------------------
    // ASIGNAR REPARTIDOR - FEIGN DELIVERY
    // --------------------------------------------------------------------
    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<List<Pedido>> obtenerPorRepartidor(@PathVariable UUID repartidorId) {
        return ResponseEntity.ok(pedidoService.findByRepartidorId(repartidorId));
    }

    @PatchMapping("/{id}/repartidor")
    public ResponseEntity<?> asignarRepartidor(
            @PathVariable UUID id,
            @RequestParam UUID repartidorId) {

        try {
            Pedido actualizado = pedidoService.asignarRepartidor(id, repartidorId);

            IniciarEntregaRequest req = new IniciarEntregaRequest();

            req.setPedidoId(id);
            req.setRepartidorId(repartidorId);

            EntregaResponse entrega = deliveryClient.iniciarEntrega(req);


            return ResponseEntity.ok(
                    new AsignarRepartidorResponse(actualizado, entrega.getCodigoConfirmacion())
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error al asignar repartidor: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // CONFIRMAR ENTREGA
    // --------------------------------------------------------------------
    @PatchMapping("/{id}/confirmar-entrega")
    public ResponseEntity<?> confirmarEntregaPedido(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(pedidoService.confirmarEntregaPedido(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body("Error al confirmar entrega: " + e.getMessage());
        }
    }
}
