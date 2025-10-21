package com.ufps.Quick_Delivery.services;

import com.ufps.Quick_Delivery.models.DeliveryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private DeliveryUserService deliveryUserService;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        DeliveryUser deliveryUser = deliveryUserService.findByCorreoAndActivo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        return new User(
                deliveryUser.getCorreo(),
                deliveryUser.getContraseña(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DELIVERY"))
        );
    }
}