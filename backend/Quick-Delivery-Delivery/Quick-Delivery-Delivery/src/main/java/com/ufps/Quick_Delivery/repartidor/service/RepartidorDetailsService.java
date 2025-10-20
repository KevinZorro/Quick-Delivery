package com.ufps.Quick_Delivery.repartidor.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ufps.Quick_Delivery.repartidor.repository.RepartidorRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de detalles de usuario para la autenticación.
 * Implementa UserDetailsService para cargar los detalles del repartidor
 * por su email (username) durante el proceso de login de Spring Security.
 */

@Service
@RequiredArgsConstructor
public class RepartidorDetailsService implements UserDetailsService {

    private final RepartidorRepository repartidorRepository;

    /**
     * Carga los detalles de un repartidor por su email.
     * @param email El email del repartidor (username).
     * @return Los detalles del repartidor (implementación de UserDetails).
     * @throws UsernameNotFoundException Si no se encuentra un repartidor con ese email.
     */
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repartidorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Repartidor no encontrado con email: " + email));
    }
}