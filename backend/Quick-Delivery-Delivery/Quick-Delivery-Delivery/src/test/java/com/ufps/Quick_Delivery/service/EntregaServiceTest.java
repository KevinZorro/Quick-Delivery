package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.ConfirmarEntregaDto;
import com.ufps.Quick_Delivery.dto.IniciarEntregaDto;
import com.ufps.Quick_Delivery.exceptions.EntregaException;
import com.ufps.Quick_Delivery.models.Entrega;
import com.ufps.Quick_Delivery.repository.EntregaRepository;
import com.ufps.Quick_Delivery.services.EntregaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para EntregaService.
 * RF-R1: Confirmación de entrega con código.
 */
@ExtendWith(MockitoExtension.class)
class EntregaServiceTest {

  @Mock
  private EntregaRepository entregaRepository;

  @InjectMocks
  private EntregaService entregaService;

  private UUID pedidoId;
  private UUID repartidorId;
  private Entrega entrega;

  @BeforeEach
  void setUp() {
    pedidoId = UUID.randomUUID();
    repartidorId = UUID.randomUUID();

    entrega = new Entrega();
    entrega.setId(UUID.randomUUID());
    entrega.setPedidoId(pedidoId);
    entrega.setRepartidorId(repartidorId);
    entrega.setEstado("CON_EL_REPARTIDOR");
    entrega.setCodigoConfirmacion("ENT-ABC123");
    entrega.setHoraInicio(LocalDateTime.now().minusMinutes(30));
  }

  // -------------------------------------------------------------------------
  // RF-R1-TC01: Iniciar entrega exitosamente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-R1-TC01: iniciarEntrega crea y guarda la entrega correctamente")
  void iniciarEntrega_cuandoNoExisteEntregaPrevia_debeCrearla() {
    System.out.println("\n[RF-R1-TC01] Iniciando entrega para pedido: " + pedidoId + " | Repartidor: " + repartidorId);

    IniciarEntregaDto dto = new IniciarEntregaDto();
    dto.setPedidoId(pedidoId);
    dto.setRepartidorId(repartidorId);

    when(entregaRepository.findByPedidoId(pedidoId)).thenReturn(Optional.empty());
    when(entregaRepository.save(any(Entrega.class))).thenReturn(entrega);

    Entrega resultado = entregaService.iniciarEntrega(dto);

    assertThat(resultado).isNotNull();
    assertThat(resultado.getPedidoId()).isEqualTo(pedidoId);
    System.out.println("✅ Entrega iniciada correctamente. ID entrega: " + resultado.getId() + " | Código: " + resultado.getCodigoConfirmacion());
    verify(entregaRepository, times(1)).save(any(Entrega.class));
  }

  // -------------------------------------------------------------------------
  // RF-R1-TC02: Iniciar entrega falla si ya existe una para el pedido
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-R1-TC02: iniciarEntrega lanza excepción si el pedido ya tiene entrega")
  void iniciarEntrega_cuandoYaExiste_debeLanzarExcepcion() {
    System.out.println("\n[RF-R1-TC02] Intentando iniciar entrega para pedido que ya tiene una activa: " + pedidoId);

    IniciarEntregaDto dto = new IniciarEntregaDto();
    dto.setPedidoId(pedidoId);
    dto.setRepartidorId(repartidorId);

    when(entregaRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(entrega));

    assertThatThrownBy(() -> entregaService.iniciarEntrega(dto))
        .isInstanceOf(EntregaException.class)
        .hasMessageContaining("ya tiene una entrega iniciada");

    System.out.println("✅ Error detectado correctamente: El pedido ya tiene una entrega iniciada.");
  }

  // -------------------------------------------------------------------------
  // RF-R1-TC03: Confirmar entrega con código correcto
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-R1-TC03: confirmarEntrega actualiza estado a ENTREGADO con código correcto")
  void confirmarEntrega_conCodigoCorrecto_debeActualizarEstado() {
    System.out.println("\n[RF-R1-TC03] Confirmando entrega del pedido: " + pedidoId + " con código: ENT-ABC123");

    ConfirmarEntregaDto dto = new ConfirmarEntregaDto();
    dto.setPedidoId(pedidoId);
    dto.setCodigoEntrega("ENT-ABC123");
    dto.setComentarios("Entregado en portería");

    Entrega entregaConfirmada = new Entrega();
    entregaConfirmada.setId(entrega.getId());
    entregaConfirmada.setPedidoId(pedidoId);
    entregaConfirmada.setEstado("ENTREGADO");
    entregaConfirmada.setDuracionMinutos(30L);

    when(entregaRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(entrega));
    when(entregaRepository.save(any(Entrega.class))).thenReturn(entregaConfirmada);

    Entrega resultado = entregaService.confirmarEntrega(dto);

    assertThat(resultado.getEstado()).isEqualTo("ENTREGADO");
    assertThat(resultado.getDuracionMinutos()).isNotNull();
    System.out.println("✅ Entrega confirmada correctamente. Estado: " + resultado.getEstado() + " | Duración: " + resultado.getDuracionMinutos() + " minutos");
    verify(entregaRepository, times(1)).save(any(Entrega.class));
  }

  // -------------------------------------------------------------------------
  // RF-R1-TC04: Confirmar entrega con código incorrecto
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-R1-TC04: confirmarEntrega lanza excepción con código incorrecto")
  void confirmarEntrega_conCodigoIncorrecto_debeLanzarExcepcion() {
    System.out.println("\n[RF-R1-TC04] Intentando confirmar entrega del pedido: " + pedidoId + " con código incorrecto: CODIGO-MALO");

    ConfirmarEntregaDto dto = new ConfirmarEntregaDto();
    dto.setPedidoId(pedidoId);
    dto.setCodigoEntrega("CODIGO-MALO");

    when(entregaRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(entrega));

    assertThatThrownBy(() -> entregaService.confirmarEntrega(dto))
        .isInstanceOf(EntregaException.class)
        .hasMessageContaining("Código de entrega incorrecto");

    System.out.println("✅ Error detectado correctamente: Código de entrega incorrecto.");
  }

  // -------------------------------------------------------------------------
  // RF-R1-TC05: Confirmar entrega ya confirmada anteriormente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-R1-TC05: confirmarEntrega lanza excepción si ya fue confirmada")
  void confirmarEntrega_cuandoYaEntregada_debeLanzarExcepcion() {
    entrega.setEstado("ENTREGADO");
    System.out.println("\n[RF-R1-TC05] Intentando confirmar entrega que ya fue entregada. Estado actual: " + entrega.getEstado());

    ConfirmarEntregaDto dto = new ConfirmarEntregaDto();
    dto.setPedidoId(pedidoId);
    dto.setCodigoEntrega("ENT-ABC123");

    when(entregaRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(entrega));

    assertThatThrownBy(() -> entregaService.confirmarEntrega(dto))
        .isInstanceOf(EntregaException.class)
        .hasMessageContaining("ya fue confirmada");

    System.out.println("✅ Error detectado correctamente: Esta entrega ya fue confirmada anteriormente.");
  }

  // -------------------------------------------------------------------------
  // RF-R1-TC06: Confirmar entrega de pedido inexistente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-R1-TC06: confirmarEntrega lanza excepción si no existe la entrega")
  void confirmarEntrega_cuandoNoExiste_debeLanzarExcepcion() {
    UUID pedidoInexistente = UUID.randomUUID();
    System.out.println("\n[RF-R1-TC06] Intentando confirmar entrega de pedido inexistente: " + pedidoInexistente);

    ConfirmarEntregaDto dto = new ConfirmarEntregaDto();
    dto.setPedidoId(pedidoInexistente);
    dto.setCodigoEntrega("ENT-ABC123");

    when(entregaRepository.findByPedidoId(any(UUID.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> entregaService.confirmarEntrega(dto))
        .isInstanceOf(EntregaException.class)
        .hasMessageContaining("Entrega no encontrada");

    System.out.println("✅ Error detectado correctamente: Entrega no encontrada para el pedido.");
  }

  // -------------------------------------------------------------------------
  // RF-R1-TC07: Verificar cálculo de duración en minutos
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-R1-TC07: confirmarEntrega calcula la duración en minutos correctamente")
  void confirmarEntrega_debeCalcularDuracionEnMinutos() {
    entrega.setHoraInicio(LocalDateTime.now().minusMinutes(45));
    System.out.println("\n[RF-R1-TC07] Confirmando entrega para verificar cálculo de duración. Inicio hace: 45 minutos");

    ConfirmarEntregaDto dto = new ConfirmarEntregaDto();
    dto.setPedidoId(pedidoId);
    dto.setCodigoEntrega("ENT-ABC123");

    when(entregaRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(entrega));
    when(entregaRepository.save(any(Entrega.class))).thenAnswer(inv -> inv.getArgument(0));

    Entrega resultado = entregaService.confirmarEntrega(dto);

    assertThat(resultado.getDuracionMinutos()).isGreaterThanOrEqualTo(44L);
    assertThat(resultado.getEstado()).isEqualTo("ENTREGADO");
    System.out.println("✅ Duración calculada correctamente: " + resultado.getDuracionMinutos() + " minutos | Estado: " + resultado.getEstado());
  }
}
