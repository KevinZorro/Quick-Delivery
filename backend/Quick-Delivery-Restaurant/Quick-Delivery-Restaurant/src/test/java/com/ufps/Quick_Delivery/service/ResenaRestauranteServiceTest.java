package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.NuevaResenaRestauranteDto;
import com.ufps.Quick_Delivery.model.ResenaRestaurante;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.ResenaRestauranteRepository;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ResenaRestauranteService.
 * RF-RS7: Registro de reseñas de restaurantes.
 * RF-RS8: Cálculo del promedio de satisfacción del restaurante.
 */
@ExtendWith(MockitoExtension.class)
class ResenaRestauranteServiceTest {

  @Mock
  private ResenaRestauranteRepository resenaRepo;

  @Mock
  private RestauranteRepository restauranteRepo;

  @InjectMocks
  private ResenaRestauranteService resenaService;

  private UUID restauranteId;
  private UUID clienteId;
  private UUID pedidoId;
  private Restaurante restaurante;
  private ResenaRestaurante resena;

  @BeforeEach
  void setUp() {
    restauranteId = UUID.randomUUID();
    clienteId = UUID.randomUUID();
    pedidoId = UUID.randomUUID();

    restaurante = Restaurante.builder()
        .id(restauranteId)
        .calificacionPromedio(0.0)
        .build();

    resena = ResenaRestaurante.builder()
        .id(UUID.randomUUID())
        .restauranteId(restauranteId)
        .clienteId(clienteId)
        .pedidoId(pedidoId)
        .calificacion(5)
        .comentario("Excelente servicio")
        .fechaCreacion(LocalDateTime.now())
        .build();
  }

  // -------------------------------------------------------------------------
  // RF-RS7-TC01: Registrar reseña válida exitosamente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS7-TC01: registrarResena guarda la reseña y actualiza promedio")
  void registrarResena_valida_debeGuardarYActualizarPromedio() {
    NuevaResenaRestauranteDto dto = new NuevaResenaRestauranteDto();
    dto.setRestauranteId(restauranteId);
    dto.setClienteId(clienteId);
    dto.setPedidoId(pedidoId);
    dto.setCalificacion(5);
    dto.setComentario("Excelente servicio");

    System.out.println("\n[RF-RS7-TC01] Registrando reseña para restaurante: " + restauranteId + " | Calificación: " + dto.getCalificacion() + " estrellas | Comentario: '" + dto.getComentario() + "'");

    when(resenaRepo.existsByPedidoId(pedidoId)).thenReturn(false);
    when(resenaRepo.save(any(ResenaRestaurante.class))).thenReturn(resena);
    when(resenaRepo.findByRestauranteIdOrderByFechaCreacionDesc(restauranteId))
        .thenReturn(List.of(resena));
    when(restauranteRepo.findById(restauranteId)).thenReturn(Optional.of(restaurante));
    when(restauranteRepo.save(any(Restaurante.class))).thenReturn(restaurante);

    resenaService.registrarResena(dto);

    System.out.println("✅ Reseña registrada correctamente. Promedio del restaurante actualizado.");
    verify(resenaRepo, times(1)).save(any(ResenaRestaurante.class));
    verify(restauranteRepo, times(1)).save(any(Restaurante.class));
  }

  // -------------------------------------------------------------------------
  // RF-RS7-TC02: Registrar reseña con calificación inválida (< 1)
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS7-TC02: registrarResena lanza excepción con calificación menor a 1")
  void registrarResena_conCalificacionMenorAUno_debeLanzarExcepcion() {
    NuevaResenaRestauranteDto dto = new NuevaResenaRestauranteDto();
    dto.setRestauranteId(restauranteId);
    dto.setClienteId(clienteId);
    dto.setPedidoId(pedidoId);
    dto.setCalificacion(0);

    System.out.println("\n[RF-RS7-TC02] Intentando registrar reseña con calificación inválida: " + dto.getCalificacion() + " estrella(s)");

    assertThatThrownBy(() -> resenaService.registrarResena(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Calificación debe ser de 1 a 5");

    System.out.println("✅ Error detectado correctamente: Calificación debe ser de 1 a 5 estrellas. Se recibió: " + dto.getCalificacion());
  }

  // -------------------------------------------------------------------------
  // RF-RS7-TC03: Registrar reseña con calificación inválida (> 5)
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS7-TC03: registrarResena lanza excepción con calificación mayor a 5")
  void registrarResena_conCalificacionMayorACinco_debeLanzarExcepcion() {
    NuevaResenaRestauranteDto dto = new NuevaResenaRestauranteDto();
    dto.setRestauranteId(restauranteId);
    dto.setClienteId(clienteId);
    dto.setPedidoId(pedidoId);
    dto.setCalificacion(6);

    System.out.println("\n[RF-RS7-TC03] Intentando registrar reseña con calificación inválida: " + dto.getCalificacion() + " estrella(s)");

    assertThatThrownBy(() -> resenaService.registrarResena(dto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Calificación debe ser de 1 a 5");

    System.out.println("✅ Error detectado correctamente: Calificación debe ser de 1 a 5 estrellas. Se recibió: " + dto.getCalificacion());
  }

  // -------------------------------------------------------------------------
  // RF-RS7-TC04: Registrar reseña duplicada (mismo pedido)
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS7-TC04: registrarResena lanza excepción si ya existe reseña para el pedido")
  void registrarResena_duplicada_debeLanzarExcepcion() {
    NuevaResenaRestauranteDto dto = new NuevaResenaRestauranteDto();
    dto.setRestauranteId(restauranteId);
    dto.setClienteId(clienteId);
    dto.setPedidoId(pedidoId);
    dto.setCalificacion(4);

    System.out.println("\n[RF-RS7-TC04] Intentando registrar reseña duplicada para pedido: " + pedidoId);

    when(resenaRepo.existsByPedidoId(pedidoId)).thenReturn(true);

    assertThatThrownBy(() -> resenaService.registrarResena(dto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Ya existe reseña para este pedido");

    System.out.println("✅ Error detectado correctamente: Ya existe reseña para este pedido.");
    verify(resenaRepo, never()).save(any());
  }

  // -------------------------------------------------------------------------
  // RF-RS8-TC01: Listar opiniones de un restaurante
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS8-TC01: listarOpinionesRestaurante retorna las reseñas del restaurante")
  void listarOpinionesRestaurante_debeRetornarLista() {
    System.out.println("\n[RF-RS8-TC01] Listando opiniones del restaurante: " + restauranteId);

    ResenaRestaurante resena2 = ResenaRestaurante.builder()
        .id(UUID.randomUUID())
        .restauranteId(restauranteId)
        .clienteId(UUID.randomUUID())
        .pedidoId(UUID.randomUUID())
        .calificacion(3)
        .comentario("Bueno pero puede mejorar")
        .fechaCreacion(LocalDateTime.now().minusDays(1))
        .build();

    when(resenaRepo.findByRestauranteIdOrderByFechaCreacionDesc(restauranteId))
        .thenReturn(List.of(resena, resena2));

    List<ResenaRestaurante> resultado = resenaService.listarOpinionesRestaurante(restauranteId);

    assertThat(resultado).hasSize(2);
    System.out.println("✅ Se encontraron " + resultado.size() + " opinión(es) del restaurante:");
    resultado.forEach(r -> System.out.println("   - " + r.getCalificacion() + " estrella(s): '" + r.getComentario() + "'"));
  }

  // -------------------------------------------------------------------------
  // RF-RS8-TC02: Listar opiniones cuando no hay reseñas
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS8-TC02: listarOpinionesRestaurante retorna lista vacía si no hay reseñas")
  void listarOpinionesRestaurante_sinResenas_debeRetornarListaVacia() {
    System.out.println("\n[RF-RS8-TC02] Listando opiniones de restaurante sin reseñas: " + restauranteId);
    when(resenaRepo.findByRestauranteIdOrderByFechaCreacionDesc(restauranteId))
        .thenReturn(List.of());

    List<ResenaRestaurante> resultado = resenaService.listarOpinionesRestaurante(restauranteId);

    assertThat(resultado).isEmpty();
    System.out.println("✅ Resultado correcto: no hay opiniones registradas para este restaurante.");
  }

  // -------------------------------------------------------------------------
  // RF-RS8-TC03: Verificar que el promedio se actualiza al registrar reseña
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS8-TC03: registrarResena actualiza el promedio de calificación del restaurante")
  void registrarResena_debeActualizarPromedioCorrectamente() {
    ResenaRestaurante resena5 = buildResena(5);
    ResenaRestaurante resena3 = buildResena(3);

    NuevaResenaRestauranteDto dto = new NuevaResenaRestauranteDto();
    dto.setRestauranteId(restauranteId);
    dto.setClienteId(clienteId);
    dto.setPedidoId(pedidoId);
    dto.setCalificacion(3);

    System.out.println("\n[RF-RS8-TC03] Registrando reseña. Reseñas existentes: [5, 3]. Promedio esperado: 4.0");

    when(resenaRepo.existsByPedidoId(pedidoId)).thenReturn(false);
    when(resenaRepo.save(any(ResenaRestaurante.class))).thenReturn(resena3);
    when(resenaRepo.findByRestauranteIdOrderByFechaCreacionDesc(restauranteId))
        .thenReturn(List.of(resena5, resena3));
    when(restauranteRepo.findById(restauranteId)).thenReturn(Optional.of(restaurante));
    when(restauranteRepo.save(any(Restaurante.class))).thenAnswer(inv -> {
      Restaurante r = inv.getArgument(0);
      assertThat(r.getCalificacionPromedio()).isEqualTo(4.0);
      System.out.println("✅ Promedio calculado y guardado correctamente: " + r.getCalificacionPromedio() + " estrellas");
      return r;
    });

    resenaService.registrarResena(dto);

    verify(restauranteRepo, times(1)).save(any(Restaurante.class));
  }

  private ResenaRestaurante buildResena(int calificacion) {
    return ResenaRestaurante.builder()
        .id(UUID.randomUUID())
        .restauranteId(restauranteId)
        .clienteId(UUID.randomUUID())
        .pedidoId(UUID.randomUUID())
        .calificacion(calificacion)
        .fechaCreacion(LocalDateTime.now())
        .build();
  }
}
