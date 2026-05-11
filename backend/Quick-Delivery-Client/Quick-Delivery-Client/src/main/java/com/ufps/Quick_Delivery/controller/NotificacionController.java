package com.ufps.Quick_Delivery.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ufps.Quick_Delivery.service.NotificacionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping(value = "/pedidos/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPedidos(@RequestParam UUID usuarioId) {
        return notificacionService.suscribirCliente(usuarioId);
    }
}
