package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.UsuarioClient;
import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ClienteService.
 * RF-C1: Registro y consulta de clientes.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

  @Mock
  private ClienteRepository clienteRepository;

  @Mock
  private UsuarioClient usuarioClient;

  @InjectMocks
  private ClienteService clienteService;

  private UUID clienteId;
  private UUID usuarioId;
  private Cliente cliente;

  @BeforeEach
  void setUp() {
    clienteId = UUID.randomUUID();
    usuarioId = UUID.randomUUID();
    cliente = new Cliente();
    cliente.setId(clienteId);
    cliente.setUsuarioId(usuarioId);
  }

  // -------------------------------------------------------------------------
  // RF-C1-TC01: Guardar cliente exitosamente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C1-TC01: guardarCliente retorna el cliente persistido")
  void guardarCliente_debeRetornarClienteGuardado() {
    System.out.println("\n[RF-C1-TC01] Registrando cliente con ID de usuario: " + usuarioId);
    when(clienteRepository.save(cliente)).thenReturn(cliente);

    Cliente resultado = clienteService.guardarCliente(cliente);

    assertThat(resultado).isNotNull();
    assertThat(resultado.getId()).isEqualTo(clienteId);
    System.out.println("✅ Cliente registrado correctamente. ID asignado: " + resultado.getId());
    verify(clienteRepository, times(1)).save(cliente);
  }

  // -------------------------------------------------------------------------
  // RF-C1-TC02: Buscar cliente por ID cuando existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C1-TC02: buscarPorId retorna el cliente cuando existe")
  void buscarPorId_cuandoExiste_debeRetornarCliente() {
    System.out.println("\n[RF-C1-TC02] Buscando cliente con ID: " + clienteId);
    when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

    Optional<Cliente> resultado = clienteService.buscarPorId(clienteId);

    assertThat(resultado).isPresent();
    assertThat(resultado.get().getId()).isEqualTo(clienteId);
    System.out.println("✅ Cliente encontrado correctamente. ID: " + resultado.get().getId());
  }

  // -------------------------------------------------------------------------
  // RF-C1-TC03: Buscar cliente por ID cuando no existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C1-TC03: buscarPorId retorna vacío cuando el cliente no existe")
  void buscarPorId_cuandoNoExiste_debeRetornarVacio() {
    UUID idInexistente = UUID.randomUUID();
    System.out.println("\n[RF-C1-TC03] Buscando cliente con ID inexistente: " + idInexistente);
    when(clienteRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Optional<Cliente> resultado = clienteService.buscarPorId(idInexistente);

    assertThat(resultado).isEmpty();
    System.out.println("✅ Resultado correcto: no se encontró ningún cliente con ese ID.");
  }

  // -------------------------------------------------------------------------
  // RF-C1-TC04: Eliminar cliente existente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C1-TC04: eliminarPorId elimina sin lanzar excepción cuando existe")
  void eliminarPorId_cuandoExiste_debeEliminarSinError() {
    System.out.println("\n[RF-C1-TC04] Eliminando cliente con ID: " + clienteId);
    when(clienteRepository.existsById(clienteId)).thenReturn(true);
    doNothing().when(clienteRepository).deleteById(clienteId);

    clienteService.eliminarPorId(clienteId);

    verify(clienteRepository, times(1)).deleteById(clienteId);
    System.out.println("✅ Cliente eliminado correctamente.");
  }

  // -------------------------------------------------------------------------
  // RF-C1-TC05: Eliminar cliente inexistente debe lanzar excepción
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C1-TC05: eliminarPorId lanza excepción cuando el cliente no existe")
  void eliminarPorId_cuandoNoExiste_debeLanzarExcepcion() {
    UUID idInexistente = UUID.randomUUID();
    System.out.println("\n[RF-C1-TC05] Intentando eliminar cliente inexistente con ID: " + idInexistente);
    when(clienteRepository.existsById(idInexistente)).thenReturn(false);

    assertThatThrownBy(() -> clienteService.eliminarPorId(idInexistente))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No existe cliente con ID");

    System.out.println("✅ Error detectado correctamente: No existe cliente con ID: " + idInexistente);
    verify(clienteRepository, never()).deleteById(any());
  }

  // -------------------------------------------------------------------------
  // RF-C1-TC06: Buscar cliente por usuarioId
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C1-TC06: buscarPorUsuarioId retorna el cliente vinculado al usuario")
  void buscarPorUsuarioId_debeRetornarClienteAsociado() {
    System.out.println("\n[RF-C1-TC06] Buscando cliente por usuarioId: " + usuarioId);
    when(clienteRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(cliente));

    Optional<Cliente> resultado = clienteService.buscarPorUsuarioId(usuarioId);

    assertThat(resultado).isPresent();
    assertThat(resultado.get().getUsuarioId()).isEqualTo(usuarioId);
    System.out.println("✅ Cliente encontrado para el usuario. ID cliente: " + resultado.get().getId());
  }
}
