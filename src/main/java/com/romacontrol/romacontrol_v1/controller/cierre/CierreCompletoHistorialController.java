package com.romacontrol.romacontrol_v1.controller.cierre;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.service.cierre.CierreCompletoHistorialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cierre-completo/historial")
@RequiredArgsConstructor
public class CierreCompletoHistorialController {

    private final CierreCompletoHistorialService historialService;

    /**
     * Ejemplo de llamada:
     * GET /api/cierre-completo/historial?desde=2025-11-01&hasta=2025-11-16
     */
    @GetMapping
    public ResponseEntity<?> obtenerHistorial(
            @RequestParam("desde") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam("hasta")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        if (desde == null || hasta == null) {
            return ResponseEntity.badRequest().body("Debés enviar las fechas 'desde' y 'hasta'.");
        }

        if (hasta.isBefore(desde)) {
            return ResponseEntity.badRequest().body("La fecha 'hasta' no puede ser menor que 'desde'.");
        }

        var lista = historialService.historial(desde, hasta);
        return ResponseEntity.ok(lista);
    }
}
