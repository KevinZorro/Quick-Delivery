package com.ufps.Quick_Delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
         // Permite el acceso libre a todas las rutas (ninguna requiere autenticación)
          .authorizeHttpRequests(authz -> authz
             .anyRequest().permitAll()
          )
          // Deshabilita la protección CSRF para todas las rutas
          // Deshabilitar CSRF para la consola H2 (mejor que deshabilitar CSRF globalmente)
          .csrf(csrf -> csrf.ignoringRequestMatchers("/**"));

          // Construye y aplica la configuración de seguridad
        return http.build();
    }
}

// Esta clase configura la seguridad de la aplicación con Spring Security, permitiendo 
// acceso libre a todas las rutas y deshabilitando la protección CSRF. Es una configuración común durante el 
// desarrollo o pruebas, ya que elimina las restricciones de seguridad.