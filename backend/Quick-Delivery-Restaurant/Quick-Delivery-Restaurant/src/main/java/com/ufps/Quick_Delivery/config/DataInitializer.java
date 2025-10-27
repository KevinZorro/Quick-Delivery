package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.dto.RegisterRequest;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.service.ProductoService;
import com.ufps.Quick_Delivery.service.RestauranteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(RestauranteService restauranteService, ProductoService productoService) {
        return args -> {

            // Crear restaurante 1
            RegisterRequest r1 = new RegisterRequest();
            r1.setCorreo("restaurante1@demo.com");
            r1.setPassword("password123");
            r1.setNombre("Restaurante Uno");
            r1.setDireccion("Calle 1 #123");
            r1.setTelefono("3001000001");
            r1.setDocumentosLegales("NIT1001");
            r1.setTipoCocina("Italiana");
            Restaurante restaurante1 = restauranteService.createIfNotExists(r1);

            // Crear restaurante 2
            RegisterRequest r2 = new RegisterRequest();
            r2.setCorreo("restaurante2@demo.com");
            r2.setPassword("password123");
            r2.setNombre("Restaurante Dos");
            r2.setDireccion("Calle 2 #234");
            r2.setTelefono("3001000002");
            r2.setDocumentosLegales("NIT1002");
            r2.setTipoCocina("Mexicana");
            Restaurante restaurante2 = restauranteService.createIfNotExists(r2);

            // Productos para restaurante 1
            Producto p1 = new Producto();
            p1.setNombre("Pizza Margarita");
            p1.setDescripcion("Pizza con salsa de tomate, mozzarella y albahaca.");
            p1.setPrecio(new BigDecimal("30000"));
            p1.setCategoria("Pizzas");
            p1.setDisponible(true);
            p1.setRestaurante(restaurante1);
            productoService.create(p1);

            Producto p2 = new Producto();
            p2.setNombre("Lasaña de Carne");
            p2.setDescripcion("Lasaña tradicional con carne, salsa bechamel y queso.");
            p2.setPrecio(new BigDecimal("35000"));
            p2.setCategoria("Pastas");
            p2.setDisponible(true);
            p2.setRestaurante(restaurante1);
            productoService.create(p2);

            // Productos para restaurante 2
            Producto p3 = new Producto();
            p3.setNombre("Tacos al Pastor");
            p3.setDescripcion("Tacos con carne al pastor, cebolla y cilantro.");
            p3.setPrecio(new BigDecimal("15000"));
            p3.setCategoria("Tacos");
            p3.setDisponible(true);
            p3.setRestaurante(restaurante2);
            productoService.create(p3);

            Producto p4 = new Producto();
            p4.setNombre("Quesadilla de Pollo");
            p4.setDescripcion("Quesadilla rellena de pollo y queso.");
            p4.setPrecio(new BigDecimal("18000"));
            p4.setCategoria("Quesadillas");
            p4.setDisponible(true);
            p4.setRestaurante(restaurante2);
            productoService.create(p4);
        };
    }
}
