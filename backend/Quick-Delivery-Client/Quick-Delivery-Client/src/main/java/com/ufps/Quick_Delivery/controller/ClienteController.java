package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Crear cliente
    @PostMapping
    public ResponseEntity<Cliente> create(@RequestBody Cliente cliente) {
        System.err.println("perro?");
        Cliente creado = clienteService.create(cliente);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // Listar todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> findAll() {
        List<Cliente> lista = clienteService.findAll();
        return ResponseEntity.ok(lista);
    }

    // Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable UUID id) {
        Optional<Cliente> clienteOpt = clienteService.findById(id);
        return clienteOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable UUID id, @RequestBody Cliente cliente) {
        try {
            Cliente actualizado = clienteService.update(id, cliente);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
