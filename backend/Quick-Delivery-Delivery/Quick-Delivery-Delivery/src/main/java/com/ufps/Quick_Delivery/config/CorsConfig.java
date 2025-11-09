package com.ufps.Quick_Delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Indica que esta clase contiene configuraciones para el proyecto Spring Boot
public class CorsConfig {

    @Bean // Define un componente (bean) que Spring Boot cargará automáticamente al iniciar
    public WebMvcConfigurer corsConfigurer() {
        // Retorna una configuración personalizada para el manejo de CORS
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                // Se define la política CORS para todas las rutas del backend
                registry.addMapping("/**")
                        // Lista de orígenes (dominios) permitidos para hacer peticiones al backend
                        .allowedOrigins(
                            "https://quick-delivery-84dfb.web.app", // Dominio de producción (Firebase Hosting)
                            "http://localhost:4200",               // Entorno local de Angular
                            "http://127.0.0.1:4200",
                            "http://localhost:55000",              // Otros puertos usados para pruebas
                            "http://127.0.0.1:55000",
                            "http://localhost:4300",
                            "http://127.0.0.1:4300"
                        )
                        // Métodos HTTP permitidos desde esos orígenes
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        // Se permite cualquier encabezado en la petición
                        .allowedHeaders("*")
                        // Permite el envío de credenciales (cookies, tokens, etc.)
                        .allowCredentials(true)
                        // Tiempo (en segundos) que el navegador puede mantener en caché la configuración CORS
                        .maxAge(3600);
            }
        };
    }
}

// Esta clase configura las reglas CORS para tu aplicación Spring Boot.
// Permite que tu frontend (por ejemplo en Angular) pueda comunicarse con tu backend sin ser
// bloqueado por el navegador, indicando explícitamente qué dominios, métodos HTTP y encabezados están autorizados.
