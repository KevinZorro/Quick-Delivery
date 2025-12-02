package com.ufps.Quick_Delivery.controller;

import com.ufps.Quick_Delivery.model.Promocion;
import com.ufps.Quick_Delivery.security.JwtService;
import com.ufps.Quick_Delivery.service.PromocionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.ufps.Quick_Delivery.client.RestauranteClient;
import com.ufps.Quick_Delivery.dto.PromocionUpdateRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    private final PromocionService promocionService;
    private final RestauranteClient restauranteClient;
    private final JwtService jwtService;
    
 

    public PromocionController(
            PromocionService promocionService,
            RestauranteClient restauranteClient,
            JwtService jwtService
    ) {
        this.promocionService = promocionService;
        this.restauranteClient = restauranteClient;
        this.jwtService = jwtService;
    }

@GetMapping("/codigo/{codigo}")
public ResponseEntity<?> validarCodigo(
        @PathVariable String codigo,
        @RequestParam("clienteId") UUID clienteId,
    @RequestParam(value = "restauranteId", required = false) UUID restauranteId){

    Optional<Promocion> promoOpt = promocionService.obtenerPorCodigo(codigo);
    if (promoOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Código no encontrado"));
    }
    Promocion promo = promoOpt.get();

    if (promocionService.yaUsoPromocion(promo.getId(), clienteId)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Ya has usado esta promoción"));
    }

    if (!promocionService.puedeUsarseCompleto(promo, clienteId, restauranteId)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Promoción no válida o expirada"));
    }

    promocionService.registrarUsoParaCliente(promo, clienteId, restauranteId);

    return ResponseEntity.ok(promo);
}





    @PutMapping("/id/{id}")
public Promocion actualizarPromocion(
        @PathVariable UUID id,
        @RequestBody PromocionUpdateRequest request
) {
    return promocionService.actualizarDatosPromocion(
            id,
            request.estado(),
            request.fechaExpiracion(),
            request.cantidadUsos()
    );
}


    @GetMapping
    public List<Promocion> getAllPromociones() {
        return promocionService.getAllPromociones();
    }

    
    @GetMapping("/id/{id}")
    public Optional<Promocion> getPromocionById(@PathVariable UUID id) {
        return promocionService.getPromocionById(id);
    }

    @GetMapping("/restaurante")
    public List<Promocion> getMisPromociones(HttpServletRequest request) {
        UUID userId = extraerUserIdDesdeRequest(request);
        UUID restauranteId = restauranteClient.obtenerRestaurantePorUsuarioId(userId).getId();
        return promocionService.obtenerPromocionesRestaurante(restauranteId);
    }

    @PostMapping
    public Promocion crearPromocion(@RequestBody Promocion promocion, HttpServletRequest request) {
        UUID userId = extraerUserIdDesdeRequest(request);
        UUID restauranteId = restauranteClient.obtenerRestaurantePorUsuarioId(userId).getId();
        return promocionService.crearPromocion(promocion, restauranteId);
    }

    private UUID extraerUserIdDesdeRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Token no presente");
        }
        String token = authHeader.substring(7);
        return jwtService.getUserIdFromToken(token); // ya lo usas en el filtro
    }

    @DeleteMapping("/{id}")
    public void deletePromocion(@PathVariable UUID id) {
        promocionService.deletePromocion(id);
    }
}
