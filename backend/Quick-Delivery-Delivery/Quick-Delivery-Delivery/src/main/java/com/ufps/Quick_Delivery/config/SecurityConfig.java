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
          // Permitir acceso libre a la consola H2
          .authorizeHttpRequests(authz -> authz
             .anyRequest().permitAll()
          )
          // Deshabilitar CSRF para la consola H2 (mejor que deshabilitar CSRF globalmente)
          .csrf(csrf -> csrf.ignoringRequestMatchers("/**"));

        return http.build();
    }
}