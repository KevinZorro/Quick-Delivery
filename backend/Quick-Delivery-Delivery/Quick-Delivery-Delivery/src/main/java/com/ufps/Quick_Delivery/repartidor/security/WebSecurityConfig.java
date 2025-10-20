package com.ufps.Quick_Delivery.repartidor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ufps.Quick_Delivery.repartidor.service.RepartidorDetailsService;

import lombok.RequiredArgsConstructor;

/**
 * Clase de configuración principal para Spring Security.
 * Define el SecurityFilterChain para deshabilitar CSRF, configurar la autenticación JWT
 * sin estado y asegurar los endpoints.
 * @author Ranita_Dardo_Dorada (Erika Sanchez)
 */

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final RepartidorDetailsService repartidorDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler; // Inyectado automáticamente por @Component
    
    /**
     * Define el filtro JWT que se ejecutará antes del filtro estándar de Spring Security.
     * @return Una nueva instancia del filtro de token JWT.
     */
    
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Define el codificador de contraseñas. Se utiliza BCrypt.
     * @return El codificador de contraseñas.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el proveedor de autenticación DAO (Data Access Object) que usará nuestro RepartidorDetailsService.
     * @return El proveedor de autenticación configurado.
     */

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(repartidorDetailsService); 
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * Define el gestor de autenticación principal de Spring Security.
     * @param authConfig La configuración de autenticación.
     * @return El gestor de autenticación.
     * @throws Exception Si ocurre un error al obtener el gestor.
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    /**
     * Define la cadena de filtros de seguridad para todas las peticiones HTTP.
     * Configura el manejo de excepciones, la política de sesión y las reglas de autorización.
     * @param http El objeto HttpSecurity a configurar.
     * @return La cadena de filtros de seguridad.
     * @throws Exception Si ocurre un error al configurar la cadena.
     */
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Permitir acceso al endpoint de LOGIN sin autenticación
                    .requestMatchers("/api/auth/repartidor/login").permitAll() 
                    .anyRequest().authenticated() // Cualquier otra petición requiere token
            );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}