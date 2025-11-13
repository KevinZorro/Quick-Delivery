package com.ufps.Quick_Delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.ufps.Quick_Delivery.client")
@SpringBootApplication
public class QuickDeliveryEdgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickDeliveryEdgeApplication.class, args);
    }
}
