package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.model.Categoria;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.service.ProductoService;
import com.ufps.Quick_Delivery.service.RestauranteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(RestauranteService restauranteService, ProductoService productoService) {
        return args -> {

            // Simulamos dos usuarios diferentes (en producción estos vendrían de la entidad Usuario)
            UUID usuario1Id = UUID.randomUUID();
            UUID usuario2Id = UUID.randomUUID();

            // Crear restaurante 1 - Italiano
            Restaurante restaurante1 = Restaurante.builder()
                    .usuarioId(usuario1Id)
                    .descripcion("Auténtica cocina italiana con recetas tradicionales y ingredientes importados")
                    .categoria(Categoria.ITALIANA)
                    .calificacionPromedio(4.5)
                    .imagenUrl("/assets/images/restaurante-italiano.jpg")
                    .build();
            restaurante1 = restauranteService.crear(restaurante1);

            // Crear restaurante 2 - Mexicano
            Restaurante restaurante2 = Restaurante.builder()
                    .usuarioId(usuario2Id)
                    .descripcion("Sabores mexicanos auténticos con las mejores salsas caseras")
                    .categoria(Categoria.MEXICANA)
                    .calificacionPromedio(4.7)
                    .imagenUrl("/assets/images/restaurante-mexicano.jpg")
                    .build();
            restaurante2 = restauranteService.crear(restaurante2);

            // Crear restaurante 3 - Comida Rápida (mismo usuario que restaurante 1)
            Restaurante restaurante3 = Restaurante.builder()
                    .usuarioId(usuario1Id)
                    .descripcion("Las mejores hamburguesas y papas fritas de la ciudad")
                    .categoria(Categoria.COMIDA_RAPIDA)
                    .calificacionPromedio(4.2)
                    .imagenUrl("/assets/images/restaurante-comida-rapida.jpg")
                    .build();
            restaurante3 = restauranteService.crear(restaurante3);

            // Productos para restaurante 1 (Italiano)
            Producto p1 = new Producto();
            p1.setNombre("Pizza Margarita");
            p1.setDescripcion("Pizza con salsa de tomate, mozzarella fresca y albahaca.");
            p1.setPrecio(new BigDecimal("30000"));
            p1.setCategoria("Pizzas");
            p1.setDisponible(true);
            p1.setRestaurante(restaurante1);
            productoService.create(p1);

            Producto p2 = new Producto();
            p2.setNombre("Lasaña de Carne");
            p2.setDescripcion("Lasaña tradicional con carne, salsa bechamel y queso parmesano.");
            p2.setPrecio(new BigDecimal("35000"));
            p2.setCategoria("Pastas");
            p2.setDisponible(true);
            p2.setRestaurante(restaurante1);
            productoService.create(p2);

            Producto p3 = new Producto();
            p3.setNombre("Risotto ai Funghi");
            p3.setDescripcion("Risotto cremoso con hongos porcini y queso parmesano.");
            p3.setPrecio(new BigDecimal("32000"));
            p3.setCategoria("Pastas");
            p3.setDisponible(true);
            p3.setRestaurante(restaurante1);
            productoService.create(p3);

            // Productos para restaurante 2 (Mexicano)
            Producto p4 = new Producto();
            p4.setNombre("Tacos al Pastor");
            p4.setDescripcion("Tacos con carne al pastor, cebolla, cilantro y piña.");
            p4.setPrecio(new BigDecimal("15000"));
            p4.setCategoria("Tacos");
            p4.setDisponible(true);
            p4.setRestaurante(restaurante2);
            productoService.create(p4);

            Producto p5 = new Producto();
            p5.setNombre("Quesadilla de Pollo");
            p5.setDescripcion("Quesadilla rellena de pollo marinado y queso Oaxaca.");
            p5.setPrecio(new BigDecimal("18000"));
            p5.setCategoria("Quesadillas");
            p5.setDisponible(true);
            p5.setRestaurante(restaurante2);
            productoService.create(p5);

            Producto p6 = new Producto();
            p6.setNombre("Burrito de Carne Asada");
            p6.setDescripcion("Burrito con carne asada, frijoles, arroz, guacamole y pico de gallo.");
            p6.setPrecio(new BigDecimal("22000"));
            p6.setCategoria("Burritos");
            p6.setDisponible(true);
            p6.setRestaurante(restaurante2);
            productoService.create(p6);

            // Productos para restaurante 3 (Comida Rápida)
            Producto p7 = new Producto();
            p7.setNombre("Hamburguesa Clásica");
            p7.setDescripcion("Hamburguesa con carne 100% res, lechuga, tomate y queso cheddar.");
            p7.setPrecio(new BigDecimal("20000"));
            p7.setCategoria("Hamburguesas");
            p7.setDisponible(true);
            p7.setRestaurante(restaurante3);
            productoService.create(p7);

            Producto p8 = new Producto();
            p8.setNombre("Papas Fritas Grandes");
            p8.setDescripcion("Papas fritas crujientes con sal marina.");
            p8.setPrecio(new BigDecimal("8000"));
            p8.setCategoria("Acompañamientos");
            p8.setDisponible(true);
            p8.setRestaurante(restaurante3);
            productoService.create(p8);

            Producto p9 = new Producto();
            p9.setNombre("Nuggets de Pollo");
            p9.setDescripcion("10 nuggets de pollo empanizado con salsas incluidas.");
            p9.setPrecio(new BigDecimal("15000"));
            p9.setCategoria("Pollo");
            p9.setDisponible(true);
            p9.setRestaurante(restaurante3);
            productoService.create(p9);

            System.out.println("=== Datos de prueba cargados exitosamente ===");
            System.out.println("Restaurante 1 (Usuario " + usuario1Id + "): " + restaurante1.getDescripcion());
            System.out.println("Restaurante 2 (Usuario " + usuario2Id + "): " + restaurante2.getDescripcion());
            System.out.println("Restaurante 3 (Usuario " + usuario1Id + "): " + restaurante3.getDescripcion());
            System.out.println("Total de productos creados: 9");
        };
    }
}
