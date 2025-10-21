package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.client.UsuarioClient;
import com.ufps.Quick_Delivery.dto.UsuarioDto;

import com.ufps.Quick_Delivery.repository.ClienteRepository;
import jakarta.validation.Valid;
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

    public Cliente guardarCliente(@Valid Cliente cliente) {
        // Aquí puedes validar si el usuario existe llamando a usuarioClient
        usuarioClient.obtenerUsuarioPorId(cliente.getUsuarioId());
        return clienteRepository.save(cliente);
    }

    public Optional<UsuarioDto> obtenerDatosUsuario(UUID usuarioId) {
        return Optional.ofNullable(usuarioClient.obtenerUsuarioPorId(usuarioId));
    }

    // Buscar cliente por Id
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(/*@NotNull */  UUID id) {
        return clienteRepository.findById(id);
    }

    // Obtener todos los clientes
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // Eliminar cliente por ID 
    public void eliminarPorId(/* @NotNull*/  UUID id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe cliente con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
