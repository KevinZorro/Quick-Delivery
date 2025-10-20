package com.ufps.Quick_Delivery.repartidor.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa a un Repartidor.
 * Mapea la tabla 'repartidores' en el esquema 'Delivery' de la base de datos PostgreSQL.
 * Implementa UserDetails para la integración con Spring Security.
 */

@Entity
@Table(name = "repartidores", schema = "Delivery")
@Data
public class Repartidor implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;

    /**
     * Define las autoridades (roles) del repartidor.
     * Por defecto, devuelve un rol fijo de 'ROLE_REPARTIDOR'.
     * @return Una colección con las autoridades concedidas al repartidor.
     */

    // Implementación mínima de Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_REPARTIDOR");
    }

    /**
     * Devuelve el email del repartidor, que se usa como nombre de usuario para la autenticación.
     * @return El email del repartidor.
     */
    
    @Override
    public String getUsername() { return this.email; }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}