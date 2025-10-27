package com.ufps.Quick_Delivery.service;

import com.ufps.Quick_Delivery.dto.RegisterRequest;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public RestauranteService(RestauranteRepository repo, BCryptPasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(@NotNull UUID id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Restaurante> listarTodos() {
        return repo.findAll();
    }

    public Optional<Restaurante> findByCorreo(String correo) {
        return repo.findByCorreo(correo);
    }

    public Restaurante createIfNotExists(RegisterRequest req) {
        return repo.findByCorreo(req.getCorreo()).orElseGet(() -> {
            Restaurante r = Restaurante.builder()
                    .correo(req.getCorreo())
                    .password(passwordEncoder.encode(req.getPassword()))
                    .activo(true)
                    .intentosFallidos(0)
                    .lockedUntil(null)
                    .nombre(req.getNombre())
                    .direccion(req.getDireccion())
                    .telefono(req.getTelefono())
                    .documentosLegales(req.getDocumentosLegales())
                    .tipoCocina(req.getTipoCocina())
                    .imagenUrl(null)
                    .build();
            return repo.save(r);
        });
    }

    public boolean checkPassword(Restaurante restaurante, String rawPassword) {
        return passwordEncoder.matches(rawPassword, restaurante.getPassword());
    }

    public String attemptLogin(String correo, String rawPassword) {
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

        r.setIntentosFallidos(0);
        r.setLockedUntil(null);
        repo.save(r);
        return "OK";
    }

    public boolean closeAccount(UUID id, boolean confirm) {
        if(!confirm) return false;
        Optional<Restaurante> or = repo.findById(id);
        if(or.isEmpty()) return false;

        Restaurante r = or.get();
        r.setActivo(false);
        repo.save(r);
        return true;
    }

    // Registro nuevo restaurante (validando duplicados y campos obligatorios)
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

    public boolean confirmarCuenta(String correo) {
        Optional<Restaurante> or = repo.findByCorreo(correo);
        if (or.isEmpty()) return false;
        Restaurante r = or.get();
        if (r.isActivo()) return false;
        r.setActivo(true);
        repo.save(r);
        return true;
    }

    public byte[] generarReporte(Long id, ReporteRequest req) {
        if (req.getFechaInicio() == null || req.getFechaFin() == null || req.getTipoReporte() == null) {
            throw new IllegalArgumentException("Debe especificar fechaInicio, fechaFin y tipoReporte");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Reporte de Desempeño\n");
        sb.append("Restaurante ID: ").append(id).append("\n");
        sb.append("Tipo de Reporte: ").append(req.getTipoReporte()).append("\n");
        sb.append("Rango de Fechas: ").append(req.getFechaInicio()).append(" a ").append(req.getFechaFin()).append("\n\n");
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
