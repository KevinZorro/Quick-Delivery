package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Crear nuevo cliente
    public Cliente create(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // Listar todos los clientes
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    // Buscar cliente por ID
    public Optional<Cliente> findById(UUID id) {
        return clienteRepository.findById(id);
    }

    // Actualizar cliente
    public Cliente update(UUID id, Cliente clienteData) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);
        if(optionalCliente.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado");
        }
        Cliente cliente = optionalCliente.get();
        cliente.setNombre(clienteData.getNombre());
        cliente.setTelefono(clienteData.getTelefono());
        cliente.setEmail(clienteData.getEmail());
        cliente.setActivo(clienteData.getActivo());
        return clienteRepository.save(cliente);
    }

    // Eliminar cliente
    public void delete(UUID id) {
        clienteRepository.deleteById(id);
    }
}
