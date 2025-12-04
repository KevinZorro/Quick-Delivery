package com.ufps.Quick_Delivery.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufps.Quick_Delivery.client.PedidoClient;
import com.ufps.Quick_Delivery.dto.HorarioAtencionDto;
import com.ufps.Quick_Delivery.dto.InterrupcionEspecialDto;
import com.ufps.Quick_Delivery.dto.PedidoDto;
import com.ufps.Quick_Delivery.dto.RestauranteRequestDto;
import com.ufps.Quick_Delivery.dto.RestauranteResponseDto;
import com.ufps.Quick_Delivery.model.Categoria;
import com.ufps.Quick_Delivery.model.HorarioAtencion;
import com.ufps.Quick_Delivery.model.InterrupcionEspecial;
import com.ufps.Quick_Delivery.service.RestauranteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/restaurante")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final PedidoClient pedidoClient;

    @PostMapping
    public ResponseEntity<RestauranteResponseDto> crear(@Valid @RequestBody RestauranteRequestDto requestDto) {
        RestauranteResponseDto responseDto = restauranteService.crear(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDto> obtenerPorId(@PathVariable("id") UUID id) {
        RestauranteResponseDto responseDto = restauranteService.obtenerPorId(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<RestauranteResponseDto> obtenerPorUsuarioId(@PathVariable("usuarioId") UUID usuarioId) {
        RestauranteResponseDto responseDto = restauranteService.obtenerPorUsuarioId(usuarioId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<RestauranteResponseDto>> listarTodos() {
        List<RestauranteResponseDto> restaurantes = restauranteService.listarTodos();
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDto>> listarPorCategoria(@PathVariable("categoria") Categoria categoria) {
        List<RestauranteResponseDto> restaurantes = restauranteService.listarPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/calificacion/{minima}")
    public ResponseEntity<List<RestauranteResponseDto>> listarPorCalificacionMinima(@PathVariable("minima") Double minima) {
        List<RestauranteResponseDto> restaurantes = restauranteService.listarPorCalificacionMinima(minima);
        return ResponseEntity.ok(restaurantes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDto> actualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody RestauranteRequestDto requestDto) {
        RestauranteResponseDto responseDto = restauranteService.actualizar(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}/calificacion")
    public ResponseEntity<Void> actualizarCalificacion(
            @PathVariable("id") UUID id,
            @RequestParam("calificacion") Double calificacion) {
        restauranteService.actualizarCalificacion(id, calificacion);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") UUID id) {
        restauranteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{restauranteId}/pedidos/historial")
    public ResponseEntity<List<PedidoDto>> obtenerHistorialPedidos(
    @PathVariable("restauranteId") UUID restauranteId,
    @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
    @RequestParam(value = "fechaFin", required = false) String fechaFin,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "clienteId", required = false) UUID clienteId
    ) {
    List<PedidoDto> historial = pedidoClient.obtenerHistorialPedidos(
            restauranteId, fechaInicio, fechaFin, estado, clienteId
    );
    return ResponseEntity.ok(historial);
    }

@PutMapping("/pedidos/{pedidoId}/estado")
public ResponseEntity<?> actualizarEstado(
        @PathVariable("pedidoId") UUID pedidoId,
        @RequestParam("nuevoEstado") String nuevoEstado
) {
    pedidoClient.actualizarEstadoPedido(pedidoId, nuevoEstado);

    return ResponseEntity.ok(
        Map.of(
            "mensaje", "Estado actualizado correctamente",
            "pedidoId", pedidoId.toString(),
            "nuevoEstado", nuevoEstado
        )
    );
}




    @GetMapping("/{restauranteId}/historial-completo")
    public List<PedidoDto> historialCompleto(@PathVariable("restauranteId") UUID restauranteId) {
        return restauranteService.listarHistorialCompleto(restauranteId);
    }

    
    @PostMapping("/{restauranteId}/horarios")
    public ResponseEntity<?> crearHorario(
            @PathVariable UUID restauranteId,
            @RequestBody HorarioAtencionDto dto) {

        System.out.println("========== CREAR HORARIO ==========");
        System.out.println("➡ RestauranteId recibido en URL: " + restauranteId);
        System.out.println("➡ DTO recibido: " + dto);

        try {
            dto.setRestauranteId(restauranteId);

            HorarioAtencion nuevo = restauranteService
                    .getHorarioService()
                    .guardarHorario(dto);

            System.out.println("✔ Horario creado con éxito: " + nuevo.getId());
            System.out.println("====================================");

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (Exception e) {
            System.out.println("❌ ERROR al crear horario: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear horario: " + e.getMessage());
        }
    }


    @GetMapping("/{restauranteId}/horarios")
    public ResponseEntity<List<HorarioAtencion>> listarHorarios(
            @PathVariable UUID restauranteId) {

        return ResponseEntity.ok(
                restauranteService.getHorarioService().listarHorarios(restauranteId)
        );
    }

    @PutMapping("/{restauranteId}/horarios/{horarioId}")
    public ResponseEntity<HorarioAtencion> actualizarHorario(
            @PathVariable UUID restauranteId,
            @PathVariable UUID horarioId,
            @RequestBody HorarioAtencionDto dto) {

        dto.setRestauranteId(restauranteId);
        HorarioAtencion actualizado =
                restauranteService.getHorarioService().actualizar(horarioId, dto);

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{restauranteId}/horarios/{horarioId}")
    public ResponseEntity<Void> eliminarHorario(
            @PathVariable UUID restauranteId,
            @PathVariable UUID horarioId) {

        restauranteService.getHorarioService().eliminarHorario(horarioId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{restauranteId}/horarios/interrupciones")
    public ResponseEntity<InterrupcionEspecial> crearInterrupcion(
            @PathVariable UUID restauranteId,
            @RequestBody InterrupcionEspecialDto dto) {

        dto.setRestauranteId(restauranteId);
        InterrupcionEspecial nueva =
                restauranteService.getHorarioService().guardarInterrupcion(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/{restauranteId}/horarios/interrupciones")
    public ResponseEntity<List<InterrupcionEspecial>> listarInterrupciones(
            @PathVariable UUID restauranteId) {

        return ResponseEntity.ok(
                restauranteService.getHorarioService().listarInterrupciones(restauranteId)
        );
    }

    @DeleteMapping("/{restauranteId}/horarios/interrupciones/{interrupcionId}")
    public ResponseEntity<Void> eliminarInterrupcion(
            @PathVariable UUID restauranteId,
            @PathVariable UUID interrupcionId) {

        restauranteService.getHorarioService().eliminarInterrupcion(interrupcionId);
        return ResponseEntity.noContent().build();
    }



}
