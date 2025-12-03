package com.ufps.Quick_Delivery.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufps.Quick_Delivery.dto.HorarioAtencionDto;
import com.ufps.Quick_Delivery.dto.InterrupcionEspecialDto;
import com.ufps.Quick_Delivery.model.HorarioAtencion;
import com.ufps.Quick_Delivery.model.InterrupcionEspecial;
import com.ufps.Quick_Delivery.service.HorarioAtencionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioAtencionService service;

    // Horarios CRUD
    @PostMapping("/guardar")
    public HorarioAtencion guardarHorario(@RequestBody HorarioAtencionDto dto) {
        return service.guardarHorario(dto);
    }

    @GetMapping("/list/{restauranteId}")
    public List<HorarioAtencion> listarHorarios(@PathVariable UUID restauranteId) {
        return service.listarHorarios(restauranteId);
    }

    @GetMapping("/{id}")
    public HorarioAtencion obtenerHorario(@PathVariable UUID id) {
        return service.obtenerHorario(id).orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    @DeleteMapping("/{id}")
    public String eliminarHorario(@PathVariable UUID id) {
        service.eliminarHorario(id);
        return "Horario eliminado";
    }

    // Interrupciones CRUD
    @PostMapping("/interrupcion/guardar")
    public InterrupcionEspecial guardarInterrupcion(@RequestBody InterrupcionEspecialDto dto) {
        return service.guardarInterrupcion(dto);
    }

    @GetMapping("/interrupcion/list/{restauranteId}")
    public List<InterrupcionEspecial> listarInterrupciones(@PathVariable UUID restauranteId) {
        return service.listarInterrupciones(restauranteId);
    }

    @DeleteMapping("/interrupcion/{id}")
    public String eliminarInterrupcion(@PathVariable UUID id) {
        service.eliminarInterrupcion(id);
        return "Interrupcion eliminada";
    }

    // Endpoint separado para disponibilidad (útil para Feign)
    @GetMapping("/disponibilidad/{restauranteId}")
    public boolean disponibilidad(@PathVariable UUID restauranteId) {
        return service.estaDisponible(restauranteId);
    }

    @PutMapping("/actualizar/{id}")
    public HorarioAtencion actualizar(@PathVariable UUID id, @RequestBody HorarioAtencionDto dto) {
    return service.actualizar(id, dto);
    }


}