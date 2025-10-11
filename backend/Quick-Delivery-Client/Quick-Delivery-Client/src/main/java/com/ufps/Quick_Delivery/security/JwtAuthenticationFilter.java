package com.ufps.Quick_Delivery.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class JwtAuthenticationFilter {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // Permitir acceso libre a la consola H2
          .authorizeHttpRequests(authz -> authz
             .requestMatchers("/h2-console/**").permitAll()
             .anyRequest().permitAll()
          )
          // Deshabilitar CSRF para la consola H2 (mejor que deshabilitar CSRF globalmente)
          .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
          // Desactivar X-Frame-Options para permitir iframe en H2 Console
          .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
