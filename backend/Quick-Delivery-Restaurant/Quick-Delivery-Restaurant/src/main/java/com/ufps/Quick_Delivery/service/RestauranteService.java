package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.ReporteRequest;
import com.ufps.Quick_Delivery.model.Restaurante;
import com.ufps.Quick_Delivery.repository.RestauranteRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RestauranteService {

    private final RestauranteRepository repo;

    // Inyecta el passwordEncoder como bean
    private final BCryptPasswordEncoder passwordEncoder;

    public RestauranteService(RestauranteRepository repo, BCryptPasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // Buscar pedido por ID (solo lectura)
    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(@NotNull UUID id) {
        return repo.findById(id);
    }

    // Listar todos los pedidos
    @Transactional(readOnly = true)
    public List<Restaurante> listarTodos() {
        return repo.findAll();
    }

    public Restaurante findById(UUID id){
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));
    }

    public Optional<Restaurante> findByCorreo(String correo){
        return repo.findByCorreo(correo);
    }

    public Restaurante createIfNotExists(String correo, String rawPassword){
        return repo.findByCorreo(correo).orElseGet(() -> {
            Restaurante r = Restaurante.builder()
                    .correo(correo)
                    .password(passwordEncoder.encode(rawPassword))
                    .activo(true)
                    .intentosFallidos(0)
                    .lockedUntil(null)
                    .build();
            return repo.save(r);
        });
    }

    public boolean checkPassword(Restaurante restaurante, String rawPassword){
        return passwordEncoder.matches(rawPassword, restaurante.getPassword());
    }

    /**
     * HU032 - Autenticación con bloqueo tras 3 intentos fallidos consecutivos
     */
    public String attemptLogin(String correo, String rawPassword){
        Optional<Restaurante> or = repo.findByCorreo(correo);
        if (or.isEmpty()) return "CREDENCIALES_INVALIDAS";

        Restaurante r = or.get();

        if (!r.isActivo()) return "CUENTA_SUSPENDIDA";

        if (r.getLockedUntil() != null && r.getLockedUntil().isAfter(LocalDateTime.now())) {
            return "CUENTA_BLOQUEADA_TEMPORALMENTE";
        }

        if (!checkPassword(r, rawPassword)) {
            int fallos = r.getIntentosFallidos() + 1;
            r.setIntentosFallidos(fallos);
            if (fallos >= 3) {
                r.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                r.setIntentosFallidos(0);
                repo.save(r);
                return "CUENTA_BLOQUEADA_TEMPORALMENTE";
            } else {
                repo.save(r);
                return "CREDENCIALES_INVALIDAS";
            }
        }

        // Éxito: reset intentos y unlock
        r.setIntentosFallidos(0);
        r.setLockedUntil(null);
        repo.save(r);
        return "OK";
    }

    /**
     * HU034 - Suspender cuenta (requiere confirmación)
     */
    public boolean closeAccount(UUID id, boolean confirm){
        if(!confirm) return false;
        Optional<Restaurante> or = repo.findById(id);
        if(or.isEmpty()) return false;
        Restaurante r = or.get();
        r.setActivo(false);
        repo.save(r);
        return true;
    }
// HU031 es el registro de un nuevo restaurante lo q hace este metodo es validar si no hay otro con el mismo correo y lo confirma y lo guarda si todo esta bien
public Restaurante registrarNuevo(Restaurante r) {
    if (repo.findByCorreo(r.getCorreo()).isPresent()) {
        throw new IllegalArgumentException("Ya existe un restaurante con este correo");
    }

    if (r.getCorreo() == null || r.getPassword() == null || r.getNombre() == null ||
        r.getDireccion() == null || r.getTelefono() == null ||
        r.getTipoCocina() == null || r.getDocumentosLegales() == null) {
        throw new IllegalArgumentException("Todos los campos son obligatorios");
    }

    r.setPassword(passwordEncoder.encode(r.getPassword()));
    r.setActivo(false);
    r.setIntentosFallidos(0);
    r.setLockedUntil(null);

    return repo.save(r);
}

// HU031 - Confirmar cuenta o activarla
public boolean confirmarCuenta(String correo) {
    Optional<Restaurante> or = repo.findByCorreo(correo);
    if (or.isEmpty()) return false;

    Restaurante r = or.get();
    if (r.isActivo()) return false;

    r.setActivo(true);
    repo.save(r);
    return true;
}
// HU07 generar resportes
public byte[] generarReporte(Long id, ReporteRequest req) {
    if (req.getFechaInicio() == null || req.getFechaFin() == null || req.getTipoReporte() == null) {
        throw new IllegalArgumentException("Debe especificar fechaInicio, fechaFin y tipoReporte");
    }

    // Encabezado del reporte
    StringBuilder sb = new StringBuilder();
    sb.append("Reporte de Desempeño\n");
    sb.append("Restaurante ID: ").append(id).append("\n");
    sb.append("Tipo de Reporte: ").append(req.getTipoReporte()).append("\n");
    sb.append("Rango de Fechas: ").append(req.getFechaInicio()).append(" a ").append(req.getFechaFin()).append("\n\n");

    // Columnas CSV
    sb.append("Fecha,Descripción,Métrica,Valor\n");

    for (int i = 1; i <= 5; i++) {
        sb.append(req.getFechaInicio().plusDays(i))
          .append(",Ejemplo de registro ")
          .append(i)
          .append(",")
          .append(req.getTipoReporte())
          .append(",")
          .append((int) (Math.random() * 100))
          .append("\n");
    }
    return sb.toString().getBytes(StandardCharsets.UTF_8);
}
}
