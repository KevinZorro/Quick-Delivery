package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.DTO.UsuarioResponse;
import com.ufps.Quick_Delivery.client.UsuarioClient;
import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Validated
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente creado = clienteService.guardarCliente(cliente);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable UUID id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable UUID id) {
        clienteService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public ResponseEntity<Cliente> obtenerClientePorUsuarioId(@PathVariable UUID usuarioId) {
        return clienteService.buscarPorUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{clienteId}/contacto")
    public ResponseEntity<UsuarioResponse> obtenerContactoCliente(@PathVariable UUID clienteId) {
    UsuarioResponse contacto = clienteService.obtenerContactoCliente(clienteId);
    return ResponseEntity.ok(contacto);
}


}
