package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer {

    private final ClienteRepository clienteRepository;

    public DataInitializer(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @PostConstruct
    public void init() {
        if (clienteRepository.count() == 0) {
            Cliente cliente1 = new Cliente(UUID.randomUUID(), UUID.randomUUID());
            Cliente cliente2 = new Cliente(UUID.randomUUID(), UUID.randomUUID());
            clienteRepository.save(cliente1);
            clienteRepository.save(cliente2);
            System.out.println("Clientes inicializados en base de datos");
        }
    }
}
