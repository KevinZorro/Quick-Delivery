package com.ufps.Quick_Delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Clase principal del microservicio Restaurante.
 * 
 * Habilita la comunicación con otros microservicios usando Feign Client,
 * en este caso, para obtener los pedidos desde el microservicio 'client'.
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.ufps.Quick_Delivery.client")
public class QuickDeliveryRestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickDeliveryRestaurantApplication.class, args);
    }
}
