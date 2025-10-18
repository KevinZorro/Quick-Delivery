package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.PedidoCliente;
import com.ufps.Quick_Delivery.service.PedidoClienteService;
import com.ufps.Quick_Delivery.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.ufps.Quick_Delivery.DTO.PedidoClienteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@CrossOrigin
public class PedidoClienteController {

    private final PedidoClienteService pedidoClienteService;
    private final ClienteService clienteService;

    // Obtener todos los pedidos
    @GetMapping
    public List<PedidoCliente> getAllPedidos() {
        return pedidoClienteService.getAllPedidos();
    }

    // Obtener un pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoCliente> getPedidoById(@PathVariable UUID id) {
        Optional<PedidoCliente> pedido = pedidoClienteService.getPedidoById(id);
        return pedido.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo pedido
    @PostMapping
    public ResponseEntity<PedidoCliente> createPedido(@RequestBody @Valid PedidoClienteDTO pedidoClienteDTO) {
        PedidoCliente p = new PedidoCliente();


        // Buscar el cliente por id
        Cliente cliente = clienteService.findById(pedidoClienteDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Mapear datos desde DTO al modelo PedidoCliente
        p.setClienteId(cliente);
        p.setFechaPedido(pedidoClienteDTO.getFechaPedido());
        p.setFechaHoraEstimada(pedidoClienteDTO.getFechaHoraEstimada());
        p.setMetodoPago(pedidoClienteDTO.getMetodoPago());
        p.setTiempoEstimado(pedidoClienteDTO.getTiempoEstimado());
        p.setTotal(pedidoClienteDTO.getTotal());
        p.setEstado(pedidoClienteDTO.getEstado());
        p.setInstrucciones(pedidoClienteDTO.getInstrucciones());
        p.setProductoId(pedidoClienteDTO.getProductoId());
        p.setFechaCreacion(pedidoClienteDTO.getFechaCreacion());
        // Guardar el pedido
        PedidoCliente creado = pedidoClienteService.createPedido(p);

        return ResponseEntity.ok(creado);
    }

    // Actualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<PedidoCliente> updatePedido(@PathVariable UUID id,
            @RequestBody @Valid PedidoCliente pedidoCliente) {
        try {
            PedidoCliente actualizado = pedidoClienteService.updatePedido(id, pedidoCliente);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable UUID id) {
        try {
            pedidoClienteService.deletePedido(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
