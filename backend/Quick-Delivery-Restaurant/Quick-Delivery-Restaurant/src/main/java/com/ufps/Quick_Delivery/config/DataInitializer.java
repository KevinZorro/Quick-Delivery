package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.service.RestauranteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(RestauranteService service){
        return args -> {
            // usuario por defecto para pruebas
            service.createIfNotExists("restaurante@demo.com", "password123");
        };
    }
}
