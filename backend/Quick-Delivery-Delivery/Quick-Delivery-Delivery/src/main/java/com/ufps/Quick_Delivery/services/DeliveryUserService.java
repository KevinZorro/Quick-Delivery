package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.models.DeliveryUser;
import com.ufps.Quick_Delivery.repository.DeliveryUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryUserService {
    @Autowired
    private DeliveryUserRepository deliveryUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<DeliveryUser> findByCorreo(String correo) {
        return deliveryUserRepository.findByCorreo(correo);
    }

    public Optional<DeliveryUser> findByCorreoAndActivo(String correo) {
        return deliveryUserRepository.findByCorreoAndActivoTrue(correo);
    }

    public Boolean existsByCorreo(String correo) {
        return deliveryUserRepository.existsByCorreo(correo);
    }

    public DeliveryUser save(DeliveryUser user) {
        user.setContraseña(passwordEncoder.encode(user.getContraseña()));
        return deliveryUserRepository.save(user);
    }

    public Optional<DeliveryUser> findById(UUID id) {
        return deliveryUserRepository.findById(id);
    }

    public void softDelete(UUID id) {
        deliveryUserRepository.findById(id).ifPresent(user -> {
            user.setActivo(false);
            deliveryUserRepository.save(user);
        });
    }
}