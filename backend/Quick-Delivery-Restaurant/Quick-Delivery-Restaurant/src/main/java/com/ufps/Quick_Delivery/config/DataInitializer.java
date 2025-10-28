package com.ufps.Quick_Delivery.config;

import com.ufps.Quick_Delivery.dto.HorarioAtencionRequest;
import com.ufps.Quick_Delivery.model.Categoria;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.service.HorarioAtencionService;
import com.ufps.Quick_Delivery.service.ProductoService;
import com.ufps.Quick_Delivery.service.RestauranteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(RestauranteService restauranteService, 
                                   ProductoService productoService,
                                   HorarioAtencionService horarioAtencionService) {
        return args -> {

            // Simulamos dos usuarios diferentes (en producción estos vendrían de la entidad Usuario)
            UUID usuario1Id = UUID.randomUUID();
            UUID usuario2Id = UUID.randomUUID();

            System.out.println("=== INICIANDO CARGA DE DATOS DE PRUEBA ===");

            // ==================== RESTAURANTES ====================

            // Crear restaurante 1 - Italiano
            Restaurante restaurante1 = Restaurante.builder()
                    .usuarioId(usuario1Id)
                    .descripcion("Auténtica cocina italiana con recetas tradicionales y ingredientes importados")
                    .categoria(Categoria.ITALIANA)
                    .calificacionPromedio(4.5)
                    .imagenUrl("/assets/images/restaurante-italiano.jpg")
                    .build();
            restaurante1 = restauranteService.crear(restaurante1);
            System.out.println("✓ Restaurante 1 creado: " + restaurante1.getDescripcion());

            // Crear restaurante 2 - Mexicano
            Restaurante restaurante2 = Restaurante.builder()
                    .usuarioId(usuario2Id)
                    .descripcion("Sabores mexicanos auténticos con las mejores salsas caseras")
                    .categoria(Categoria.MEXICANA)
                    .calificacionPromedio(4.7)
                    .imagenUrl("/assets/images/restaurante-mexicano.jpg")
                    .build();
            restaurante2 = restauranteService.crear(restaurante2);
            System.out.println("✓ Restaurante 2 creado: " + restaurante2.getDescripcion());

            // Crear restaurante 3 - Comida Rápida (mismo usuario que restaurante 1)
            Restaurante restaurante3 = Restaurante.builder()
                    .usuarioId(usuario1Id)
                    .descripcion("Las mejores hamburguesas y papas fritas de la ciudad")
                    .categoria(Categoria.COMIDA_RAPIDA)
                    .calificacionPromedio(4.2)
                    .imagenUrl("/assets/images/restaurante-comida-rapida.jpg")
                    .build();
            restaurante3 = restauranteService.crear(restaurante3);
            System.out.println("✓ Restaurante 3 creado: " + restaurante3.getDescripcion());

            // ==================== PRODUCTOS ====================

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

            System.out.println("✓ 3 productos creados para Restaurante 1");

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

            System.out.println("✓ 3 productos creados para Restaurante 2");

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

            System.out.println("✓ 3 productos creados para Restaurante 3");

            // ==================== HORARIOS DE ATENCIÓN ====================

            // Horarios para Restaurante 1 (Italiano) - Lunes a Sábado
            crearHorario(horarioAtencionService, restaurante1.getId(), DayOfWeek.MONDAY, 
                        LocalTime.of(12, 0), LocalTime.of(22, 0));
            crearHorario(horarioAtencionService, restaurante1.getId(), DayOfWeek.TUESDAY, 
                        LocalTime.of(12, 0), LocalTime.of(22, 0));
            crearHorario(horarioAtencionService, restaurante1.getId(), DayOfWeek.WEDNESDAY, 
                        LocalTime.of(12, 0), LocalTime.of(22, 0));
            crearHorario(horarioAtencionService, restaurante1.getId(), DayOfWeek.THURSDAY, 
                        LocalTime.of(12, 0), LocalTime.of(22, 0));
            crearHorario(horarioAtencionService, restaurante1.getId(), DayOfWeek.FRIDAY, 
                        LocalTime.of(12, 0), LocalTime.of(23, 30));
            crearHorario(horarioAtencionService, restaurante1.getId(), DayOfWeek.SATURDAY, 
                        LocalTime.of(12, 0), LocalTime.of(23, 30));
            System.out.println("✓ 6 horarios creados para Restaurante 1 (Italiano)");

            // Horarios para Restaurante 2 (Mexicano) - Martes a Domingo
            crearHorario(horarioAtencionService, restaurante2.getId(), DayOfWeek.TUESDAY, 
                        LocalTime.of(11, 0), LocalTime.of(21, 0));
            crearHorario(horarioAtencionService, restaurante2.getId(), DayOfWeek.WEDNESDAY, 
                        LocalTime.of(11, 0), LocalTime.of(21, 0));
            crearHorario(horarioAtencionService, restaurante2.getId(), DayOfWeek.THURSDAY, 
                        LocalTime.of(11, 0), LocalTime.of(21, 0));
            crearHorario(horarioAtencionService, restaurante2.getId(), DayOfWeek.FRIDAY, 
                        LocalTime.of(11, 0), LocalTime.of(23, 0));
            crearHorario(horarioAtencionService, restaurante2.getId(), DayOfWeek.SATURDAY, 
                        LocalTime.of(11, 0), LocalTime.of(23, 0));
            crearHorario(horarioAtencionService, restaurante2.getId(), DayOfWeek.SUNDAY, 
                        LocalTime.of(12, 0), LocalTime.of(20, 0));
            System.out.println("✓ 6 horarios creados para Restaurante 2 (Mexicano)");

            // Horarios para Restaurante 3 (Comida Rápida) - Todos los días
            crearHorario(horarioAtencionService, restaurante3.getId(), DayOfWeek.MONDAY, 
                        LocalTime.of(10, 0), LocalTime.of(23, 0));
            crearHorario(horarioAtencionService, restaurante3.getId(), DayOfWeek.TUESDAY, 
                        LocalTime.of(10, 0), LocalTime.of(23, 0));
            crearHorario(horarioAtencionService, restaurante3.getId(), DayOfWeek.WEDNESDAY, 
                        LocalTime.of(10, 0), LocalTime.of(23, 0));
            crearHorario(horarioAtencionService, restaurante3.getId(), DayOfWeek.THURSDAY, 
                        LocalTime.of(10, 0), LocalTime.of(23, 0));
            crearHorario(horarioAtencionService, restaurante3.getId(), DayOfWeek.FRIDAY, 
                        LocalTime.of(10, 0), LocalTime.of(23, 59));
            crearHorario(horarioAtencionService, restaurante3.getId(), DayOfWeek.SATURDAY, 
                        LocalTime.of(10, 0), LocalTime.of(23, 59));
            crearHorario(horarioAtencionService, restaurante3.getId(), DayOfWeek.SUNDAY, 
                        LocalTime.of(10, 0), LocalTime.of(23, 0));
            System.out.println("✓ 7 horarios creados para Restaurante 3 (Comida Rápida)");

            // ==================== RESUMEN ====================
            System.out.println("\n=== DATOS DE PRUEBA CARGADOS EXITOSAMENTE ===");
            System.out.println("📍 3 Restaurantes creados");
            System.out.println("   - Usuario " + usuario1Id + ": 2 restaurantes");
            System.out.println("   - Usuario " + usuario2Id + ": 1 restaurante");
            System.out.println("🍕 9 Productos creados (3 por restaurante)");
            System.out.println("⏰ 19 Horarios de atención creados");
            System.out.println("==============================================\n");
        };
    }

    private void crearHorario(HorarioAtencionService service, UUID restauranteId, 
                              DayOfWeek dia, LocalTime apertura, LocalTime cierre) {
        HorarioAtencionRequest req = new HorarioAtencionRequest();
        req.setRestauranteId(restauranteId);
        req.setDiaSemana(dia);
        req.setApertura(apertura);
        req.setCierre(cierre);
        service.crear(req);
    }
}
