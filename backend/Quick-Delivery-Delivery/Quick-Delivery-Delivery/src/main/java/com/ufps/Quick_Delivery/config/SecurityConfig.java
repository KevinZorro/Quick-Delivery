package com.ufps.Quick_Delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ IMPORTANTE: Aplicar la configuración CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Permitir acceso libre a todas las rutas
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            // Deshabilitar CSRF
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ Usar allowedOriginPatterns en lugar de allowedOrigins
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://quick-delivery-84dfb.web.app",
            "https://quick-delivery-84dfb.firebaseapp.com",
            "http://localhost:4200",
            "http://127.0.0.1:4200",
            "http://localhost:55000",
            "http://127.0.0.1:55000",
            "http://localhost:4300",
            "http://127.0.0.1:4300"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
