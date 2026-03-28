package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.client.CuponGlobalClient;
import com.ufps.Quick_Delivery.client.DeliveryFeignClient;
import com.ufps.Quick_Delivery.client.ProductoClient;
import com.ufps.Quick_Delivery.client.RestauranteHorarioClient;
import com.ufps.Quick_Delivery.model.Cliente;
import com.ufps.Quick_Delivery.model.EstadoPedido;
import com.ufps.Quick_Delivery.model.Pedido;
import com.ufps.Quick_Delivery.repository.ClienteRepository;
import com.ufps.Quick_Delivery.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PedidoService.
 * RF-C2: Creación de pedido desde carrito.
 * RF-C3: Consulta y actualización de estado del pedido.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

  @Mock
  private PedidoRepository pedidoRepository;

  @Mock
  private ClienteRepository clienteRepository;

  @Mock
  private ProductoClient productoClient;

  @Mock
  private DeliveryFeignClient deliveryClient;

  @Mock
  private NotificacionService notificacionService;

  @Mock
  private RestauranteHorarioClient restauranteHorarioClient;

  @Mock
  private CuponGlobalClient cuponGlobalClient;

  @InjectMocks
  private PedidoService pedidoService;

  private UUID pedidoId;
  private UUID usuarioId;
  private Cliente cliente;
  private Pedido pedido;

  @BeforeEach
  void setUp() {
    pedidoId = UUID.randomUUID();
    usuarioId = UUID.randomUUID();

    cliente = new Cliente();
    cliente.setId(UUID.randomUUID());
    cliente.setUsuarioId(usuarioId);

    pedido = new Pedido();
    pedido.setId(pedidoId);
    pedido.setCliente(cliente);
    pedido.setRestauranteId(UUID.randomUUID());
    pedido.setTotal(50000);
    pedido.setEstado(EstadoPedido.INICIADO);
  }

  // -------------------------------------------------------------------------
  // RF-C2-TC01: Buscar pedido por ID cuando existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C2-TC01: buscarPorId retorna el pedido cuando existe")
  void buscarPorId_cuandoExiste_debeRetornarPedido() {
    System.out.println("\n[RF-C2-TC01] Buscando pedido con ID: " + pedidoId);
    when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

    Optional<Pedido> resultado = pedidoService.buscarPorId(pedidoId);

    assertThat(resultado).isPresent();
    assertThat(resultado.get().getId()).isEqualTo(pedidoId);
    System.out.println("✅ Pedido encontrado. ID: " + resultado.get().getId() + " | Estado: " + resultado.get().getEstado());
  }

  // -------------------------------------------------------------------------
  // RF-C2-TC02: Buscar pedido por ID cuando no existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C2-TC02: buscarPorId retorna vacío cuando el pedido no existe")
  void buscarPorId_cuandoNoExiste_debeRetornarVacio() {
    UUID idInexistente = UUID.randomUUID();
    System.out.println("\n[RF-C2-TC02] Buscando pedido con ID inexistente: " + idInexistente);
    when(pedidoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Optional<Pedido> resultado = pedidoService.buscarPorId(idInexistente);

    assertThat(resultado).isEmpty();
    System.out.println("✅ Resultado correcto: no se encontró ningún pedido con ese ID.");
  }

  // -------------------------------------------------------------------------
  // RF-C2-TC03: Guardar pedido
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C2-TC03: guardarPedido persiste y retorna el pedido")
  void guardarPedido_debePersistirYRetornarPedido() {
    System.out.println("\n[RF-C2-TC03] Guardando pedido con total: $" + pedido.getTotal());
    when(pedidoRepository.save(pedido)).thenReturn(pedido);

    Pedido resultado = pedidoService.guardarPedido(pedido);

    assertThat(resultado).isNotNull();
    assertThat(resultado.getId()).isEqualTo(pedidoId);
    System.out.println("✅ Pedido guardado correctamente. ID: " + resultado.getId() + " | Total: $" + resultado.getTotal());
    verify(pedidoRepository, times(1)).save(pedido);
  }

  // -------------------------------------------------------------------------
  // RF-C3-TC01: Actualizar estado del pedido exitosamente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C3-TC01: actualizarEstadoPedido cambia el estado correctamente")
  void actualizarEstadoPedido_debeActualizarYNotificar() {
    System.out.println("\n[RF-C3-TC01] Actualizando estado del pedido " + pedidoId + " de " + pedido.getEstado() + " a EN_COCINA");

    Pedido pedidoActualizado = new Pedido();
    pedidoActualizado.setId(pedidoId);
    pedidoActualizado.setCliente(cliente);
    pedidoActualizado.setEstado(EstadoPedido.EN_COCINA);

    when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
    when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoActualizado);
    doNothing().when(notificacionService).notificarCambioEstado(any(Pedido.class));

    Pedido resultado = pedidoService.actualizarEstadoPedido(pedidoId, EstadoPedido.EN_COCINA);

    assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.EN_COCINA);
    System.out.println("✅ Estado actualizado correctamente a: " + resultado.getEstado());
    System.out.println("✅ Notificación al cliente enviada.");
    verify(notificacionService, times(1)).notificarCambioEstado(any(Pedido.class));
  }

  // -------------------------------------------------------------------------
  // RF-C3-TC02: Actualizar estado lanza excepción si el pedido no existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C3-TC02: actualizarEstadoPedido lanza excepción si el pedido no existe")
  void actualizarEstadoPedido_cuandoNoExiste_debeLanzarExcepcion() {
    UUID idInexistente = UUID.randomUUID();
    System.out.println("\n[RF-C3-TC02] Intentando actualizar estado de pedido inexistente: " + idInexistente);
    when(pedidoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(idInexistente, EstadoPedido.EN_COCINA))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Pedido no encontrado");

    System.out.println("✅ Error detectado correctamente: Pedido no encontrado con ID: " + idInexistente);
  }

  // -------------------------------------------------------------------------
  // RF-C3-TC03: Listar pedidos por usuario
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C3-TC03: listarPorUsuario retorna los pedidos del usuario")
  void listarPorUsuario_debeRetornarPedidosDelUsuario() {
    System.out.println("\n[RF-C3-TC03] Listando pedidos del usuario: " + usuarioId);
    when(pedidoRepository.findByCliente_UsuarioIdOrderByFechaCreacionDesc(usuarioId))
        .thenReturn(List.of(pedido));

    List<Pedido> resultado = pedidoService.listarPorUsuario(usuarioId);

    assertThat(resultado).hasSize(1);
    assertThat(resultado.get(0).getId()).isEqualTo(pedidoId);
    System.out.println("✅ Se encontraron " + resultado.size() + " pedido(s) para el usuario.");
    resultado.forEach(p -> System.out.println("   - Pedido ID: " + p.getId() + " | Estado: " + p.getEstado()));
  }

  // -------------------------------------------------------------------------
  // RF-C3-TC04: Contar pedidos por usuario
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C3-TC04: contarPedidosPorUsuario retorna el conteo correcto")
  void contarPedidosPorUsuario_debeRetornarConteo() {
    System.out.println("\n[RF-C3-TC04] Contando pedidos del usuario: " + usuarioId);
    when(pedidoRepository.countByCliente_UsuarioId(usuarioId)).thenReturn(3L);

    long conteo = pedidoService.contarPedidosPorUsuario(usuarioId);

    assertThat(conteo).isEqualTo(3L);
    System.out.println("✅ Total de pedidos del usuario: " + conteo);
  }

  // -------------------------------------------------------------------------
  // RF-C3-TC05: Confirmar entrega de pedido - flujo exitoso
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C3-TC05: confirmarEntregaPedido cambia estado a ENTREGADO")
  void confirmarEntregaPedido_debeActualizarEstadoAEntregado() {
    pedido.setEstado(EstadoPedido.CON_EL_REPARTIDOR);
    System.out.println("\n[RF-C3-TC05] Confirmando entrega del pedido: " + pedidoId + " | Estado actual: " + pedido.getEstado());

    Pedido entregado = new Pedido();
    entregado.setId(pedidoId);
    entregado.setEstado(EstadoPedido.ENTREGADO);

    when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
    when(pedidoRepository.save(any(Pedido.class))).thenReturn(entregado);

    Pedido resultado = pedidoService.confirmarEntregaPedido(pedidoId);

    assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.ENTREGADO);
    System.out.println("✅ Entrega confirmada correctamente. Estado final: " + resultado.getEstado());
  }

  // -------------------------------------------------------------------------
  // RF-C3-TC06: Confirmar entrega falla si el estado no es CON_EL_REPARTIDOR
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-C3-TC06: confirmarEntregaPedido lanza excepción si estado incorrecto")
  void confirmarEntregaPedido_cuandoEstadoIncorrecto_debeLanzarExcepcion() {
    pedido.setEstado(EstadoPedido.INICIADO);
    System.out.println("\n[RF-C3-TC06] Intentando confirmar entrega con estado incorrecto: " + pedido.getEstado());
    when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

    assertThatThrownBy(() -> pedidoService.confirmarEntregaPedido(pedidoId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Solo se pueden confirmar pedidos que están con el repartidor");

    System.out.println("✅ Error detectado correctamente: Solo se pueden confirmar pedidos que están con el repartidor.");
  }
}
