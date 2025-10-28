package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.model.Rol;
import com.ufps.Quick_Delivery.model.Usuario;
import com.ufps.Quick_Delivery.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (usuarioRepository.count() == 0) {
            Usuario gerente = new Usuario();
            // No seteamos id aquí
            gerente.setNombre("Gerente Principal");
            gerente.setCorreo("gerente@empresa.com");
            gerente.setTelefono("3000000000");
            gerente.setContraseña(passwordEncoder.encode("password123"));
            gerente.setRol(Rol.RESTAURANTE);
            gerente.setActivo(true);
            gerente.setFecharegistro(LocalDateTime.now());
            usuarioRepository.save(gerente);

            // Igualmente cliente y delivery
            Usuario cliente = new Usuario();
            cliente.setNombre("Cliente Ejemplo");
            cliente.setCorreo("cliente@empresa.com");
            cliente.setTelefono("3111111111");
            cliente.setContraseña(passwordEncoder.encode("cliente123"));
            cliente.setRol(Rol.CLIENTE);
            cliente.setActivo(true);
            cliente.setFecharegistro(LocalDateTime.now());
            usuarioRepository.save(cliente);

            Usuario delivery = new Usuario();
            delivery.setNombre("Delivery Ejemplo");
            delivery.setCorreo("delivery@empresa.com");
            delivery.setTelefono("3222222222");
            delivery.setContraseña(passwordEncoder.encode("delivery123"));
            delivery.setRol(Rol.REPARTIDOR);
            delivery.setActivo(true);
            delivery.setFecharegistro(LocalDateTime.now());
            usuarioRepository.save(delivery);
        }
    }

}
