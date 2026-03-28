package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.ProductoRequestDTO;
import com.ufps.Quick_Delivery.dto.ProductoResponseDTO;
import com.ufps.Quick_Delivery.dto.ProductoUpdateDTO;
import com.ufps.Quick_Delivery.model.Producto;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.ProductoRepository;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoService.
 * RF-RS4: Gestión del catálogo de productos del restaurante.
 * RF-RS5: Cambio de disponibilidad de productos.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

  @Mock
  private ProductoRepository productoRepository;

  @Mock
  private RestauranteRepository restauranteRepository;

  @InjectMocks
  private ProductoService productoService;

  private UUID usuarioId;
  private UUID restauranteId;
  private UUID productoId;
  private Restaurante restaurante;
  private Producto producto;

  @BeforeEach
  void setUp() {
    usuarioId = UUID.randomUUID();
    restauranteId = UUID.randomUUID();
    productoId = UUID.randomUUID();

    restaurante = Restaurante.builder()
        .id(restauranteId)
        .usuarioId(usuarioId)
        .build();

    producto = new Producto();
    producto.setId(productoId);
    producto.setRestaurante(restaurante);
    producto.setNombre("Hamburguesa Clásica");
    producto.setDescripcion("Con carne, lechuga y tomate");
    producto.setPrecio(new BigDecimal("15000"));
    producto.setCategoria("Hamburguesas");
    producto.setDisponible(true);
  }

  // -------------------------------------------------------------------------
  // RF-RS4-TC01: Crear producto exitosamente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS4-TC01: crearProducto guarda y retorna el producto creado")
  void crearProducto_debeGuardarYRetornarProducto() {
    ProductoRequestDTO dto = new ProductoRequestDTO();
    dto.setUsuarioId(usuarioId);
    dto.setNombre("Hamburguesa Clásica");
    dto.setDescripcion("Con carne, lechuga y tomate");
    dto.setPrecio(new BigDecimal("15000"));
    dto.setCategoria("Hamburguesas");
    dto.setDisponible(true);

    System.out.println("\n[RF-RS4-TC01] Creando producto: '" + dto.getNombre() + "' | Precio: $" + dto.getPrecio() + " | Restaurante usuario: " + usuarioId);
    when(restauranteRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(restaurante));
    when(productoRepository.save(any(Producto.class))).thenReturn(producto);

    ProductoResponseDTO resultado = productoService.crearProducto(dto);

    assertThat(resultado).isNotNull();
    assertThat(resultado.getNombre()).isEqualTo("Hamburguesa Clásica");
    System.out.println("✅ Producto creado correctamente. Nombre: " + resultado.getNombre() + " | Precio: $" + resultado.getPrecio());
    verify(productoRepository, times(1)).save(any(Producto.class));
  }

  // -------------------------------------------------------------------------
  // RF-RS4-TC02: Crear producto falla si el restaurante no existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS4-TC02: crearProducto lanza excepción si el restaurante no existe")
  void crearProducto_cuandoRestauranteNoExiste_debeLanzarExcepcion() {
    UUID usuarioSinRestaurante = UUID.randomUUID();
    ProductoRequestDTO dto = new ProductoRequestDTO();
    dto.setUsuarioId(usuarioSinRestaurante);
    dto.setNombre("Producto Test");
    dto.setPrecio(new BigDecimal("10000"));
    dto.setDisponible(true);

    System.out.println("\n[RF-RS4-TC02] Intentando crear producto para usuario sin restaurante: " + usuarioSinRestaurante);
    when(restauranteRepository.findByUsuarioId(any(UUID.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productoService.crearProducto(dto))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Restaurante no encontrado");

    System.out.println("✅ Error detectado correctamente: Restaurante no encontrado para el usuario con ID: " + usuarioSinRestaurante);
  }

  // -------------------------------------------------------------------------
  // RF-RS4-TC03: Obtener producto por ID cuando existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS4-TC03: obtenerProductoPorId retorna el producto cuando existe")
  void obtenerProductoPorId_cuandoExiste_debeRetornarProducto() {
    System.out.println("\n[RF-RS4-TC03] Buscando producto con ID: " + productoId);
    when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

    ProductoResponseDTO resultado = productoService.obtenerProductoPorId(productoId);

    assertThat(resultado).isNotNull();
    assertThat(resultado.getId()).isEqualTo(productoId);
    System.out.println("✅ Producto encontrado. Nombre: " + resultado.getNombre() + " | Disponible: " + resultado.getDisponible());
  }

  // -------------------------------------------------------------------------
  // RF-RS4-TC04: Obtener producto por ID cuando no existe
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS4-TC04: obtenerProductoPorId lanza excepción si el producto no existe")
  void obtenerProductoPorId_cuandoNoExiste_debeLanzarExcepcion() {
    UUID idInexistente = UUID.randomUUID();
    System.out.println("\n[RF-RS4-TC04] Buscando producto con ID inexistente: " + idInexistente);
    when(productoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productoService.obtenerProductoPorId(idInexistente))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Producto no encontrado");

    System.out.println("✅ Error detectado correctamente: Producto no encontrado con ID: " + idInexistente);
  }

  // -------------------------------------------------------------------------
  // RF-RS5-TC01: Cambiar disponibilidad de producto a false
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS5-TC01: cambiarDisponibilidad desactiva el producto correctamente")
  void cambiarDisponibilidad_aFalse_debeDesactivarProducto() {
    System.out.println("\n[RF-RS5-TC01] Desactivando producto: " + productoId + " | Estado actual: disponible=" + producto.getDisponible());

    Producto productoInactivo = new Producto();
    productoInactivo.setId(productoId);
    productoInactivo.setRestaurante(restaurante);
    productoInactivo.setNombre("Hamburguesa Clásica");
    productoInactivo.setPrecio(new BigDecimal("15000"));
    productoInactivo.setDisponible(false);

    when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
    when(productoRepository.save(any(Producto.class))).thenReturn(productoInactivo);

    ProductoResponseDTO resultado = productoService.cambiarDisponibilidad(productoId, false, usuarioId);

    assertThat(resultado.getDisponible()).isFalse();
    System.out.println("✅ Disponibilidad actualizada correctamente. Producto '" + resultado.getNombre() + "' ahora está: " + (resultado.getDisponible() ? "disponible" : "no disponible"));
    verify(productoRepository, times(1)).save(any(Producto.class));
  }

  // -------------------------------------------------------------------------
  // RF-RS5-TC02: Cambiar disponibilidad falla si no tiene permiso
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS5-TC02: cambiarDisponibilidad lanza excepción si el usuario no es dueño")
  void cambiarDisponibilidad_sinPermiso_debeLanzarExcepcion() {
    UUID otroUsuarioId = UUID.randomUUID();
    System.out.println("\n[RF-RS5-TC02] Intentando cambiar disponibilidad con usuario sin permiso: " + otroUsuarioId);
    when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

    assertThatThrownBy(() -> productoService.cambiarDisponibilidad(productoId, false, otroUsuarioId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("No tienes permiso");

    System.out.println("✅ Error detectado correctamente: No tienes permiso para cambiar la disponibilidad de este producto.");
  }

  // -------------------------------------------------------------------------
  // RF-RS5-TC03: Cambiar disponibilidad de producto a true
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS5-TC03: cambiarDisponibilidad activa el producto correctamente")
  void cambiarDisponibilidad_aTrue_debeActivarProducto() {
    producto.setDisponible(false);
    System.out.println("\n[RF-RS5-TC03] Activando producto: " + productoId + " | Estado actual: disponible=" + producto.getDisponible());

    Producto productoActivo = new Producto();
    productoActivo.setId(productoId);
    productoActivo.setRestaurante(restaurante);
    productoActivo.setNombre("Hamburguesa Clásica");
    productoActivo.setPrecio(new BigDecimal("15000"));
    productoActivo.setDisponible(true);

    when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
    when(productoRepository.save(any(Producto.class))).thenReturn(productoActivo);

    ProductoResponseDTO resultado = productoService.cambiarDisponibilidad(productoId, true, usuarioId);

    assertThat(resultado.getDisponible()).isTrue();
    System.out.println("✅ Disponibilidad actualizada correctamente. Producto '" + resultado.getNombre() + "' ahora está: " + (resultado.getDisponible() ? "disponible" : "no disponible"));
  }

  // -------------------------------------------------------------------------
  // RF-RS4-TC05: Listar productos por restaurante
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS4-TC05: obtenerProductosPorRestaurante retorna lista de productos")
  void obtenerProductosPorRestaurante_debeRetornarProductos() {
    System.out.println("\n[RF-RS4-TC05] Listando productos del restaurante: " + restauranteId);
    when(productoRepository.findByRestauranteId(restauranteId)).thenReturn(List.of(producto));

    List<ProductoResponseDTO> resultado = productoService.obtenerProductosPorRestaurante(restauranteId);

    assertThat(resultado).hasSize(1);
    assertThat(resultado.get(0).getNombre()).isEqualTo("Hamburguesa Clásica");
    System.out.println("✅ Se encontraron " + resultado.size() + " producto(s):");
    resultado.forEach(p -> System.out.println("   - " + p.getNombre() + " | $" + p.getPrecio() + " | Disponible: " + p.getDisponible()));
  }

  // -------------------------------------------------------------------------
  // RF-RS4-TC06: Eliminar producto sin permiso debe fallar
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS4-TC06: eliminarProducto lanza excepción si el usuario no es dueño")
  void eliminarProducto_sinPermiso_debeLanzarExcepcion() {
    UUID otroUsuarioId = UUID.randomUUID();
    System.out.println("\n[RF-RS4-TC06] Intentando eliminar producto con usuario sin permiso: " + otroUsuarioId);
    when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

    assertThatThrownBy(() -> productoService.eliminarProducto(productoId, otroUsuarioId))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("No tienes permiso");

    System.out.println("✅ Error detectado correctamente: No tienes permiso para eliminar este producto.");
    verify(productoRepository, never()).deleteById(any());
  }

  // -------------------------------------------------------------------------
  // RF-RS4-TC07: Actualizar producto exitosamente
  // -------------------------------------------------------------------------
  @Test
  @DisplayName("RF-RS4-TC07: actualizarProducto actualiza los campos del producto")
  void actualizarProducto_debeActualizarCampos() {
    ProductoUpdateDTO updateDTO = new ProductoUpdateDTO();
    updateDTO.setNombre("Hamburguesa Doble");
    updateDTO.setDescripcion("Con doble carne");
    updateDTO.setPrecio(new BigDecimal("20000"));
    updateDTO.setCategoria("Hamburguesas");
    updateDTO.setDisponible(true);

    System.out.println("\n[RF-RS4-TC07] Actualizando producto: " + productoId + " | Nuevo nombre: '" + updateDTO.getNombre() + "' | Nuevo precio: $" + updateDTO.getPrecio());

    Producto productoActualizado = new Producto();
    productoActualizado.setId(productoId);
    productoActualizado.setRestaurante(restaurante);
    productoActualizado.setNombre("Hamburguesa Doble");
    productoActualizado.setPrecio(new BigDecimal("20000"));
    productoActualizado.setDisponible(true);

    when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
    when(productoRepository.save(any(Producto.class))).thenReturn(productoActualizado);

    ProductoResponseDTO resultado = productoService.actualizarProducto(productoId, updateDTO, usuarioId);

    assertThat(resultado.getNombre()).isEqualTo("Hamburguesa Doble");
    assertThat(resultado.getPrecio()).isEqualByComparingTo(new BigDecimal("20000"));
    System.out.println("✅ Producto actualizado correctamente. Nombre: " + resultado.getNombre() + " | Precio: $" + resultado.getPrecio());
  }
}
