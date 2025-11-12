package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public Cliente guardarCliente(@Valid @NonNull Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Buscar cliente por Id
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(@NonNull UUID id) {
        return clienteRepository.findById(id);
    }

    // Obtener todos los clientes
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // ⭐ NUEVO MÉTODO
    public Optional<Cliente> buscarPorUsuarioId(UUID usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId);
    }

    // Eliminar cliente por ID 
    public void eliminarPorId(@NonNull UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe cliente con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
