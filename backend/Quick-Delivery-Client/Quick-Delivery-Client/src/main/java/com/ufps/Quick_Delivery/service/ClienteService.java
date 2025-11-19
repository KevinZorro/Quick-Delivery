package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.UsuarioClient;
import com.ufps.Quick_Delivery.DTO.UsuarioResponse;
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
    private final UsuarioClient usuarioClient;

    public Cliente guardarCliente(@Valid @NonNull Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(@NonNull UUID id) {
        return clienteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

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

    // ⭐ NUEVO MÉTODO: Obtener contacto del cliente con datos del usuario
    public UsuarioResponse obtenerContactoCliente(UUID clienteId) {

    Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

    // Devuelve directamente el DTO del Feign Client
    return usuarioClient.obtenerUsuario(cliente.getUsuarioId());
}


}
