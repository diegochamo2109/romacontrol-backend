// src/main/java/com/romacontrol/romacontrol_v1/web/AsistenciaSocioController.java
package com.romacontrol.romacontrol_v1.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.AsistenciaResponse;
import com.romacontrol.romacontrol_v1.service.AsistenciaSocioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class AsistenciaSocioController {

    private final AsistenciaSocioService asistenciaSocioService;

    /**
     * Devuelve solo las asistencias del usuario autenticado.
     */
    @GetMapping("/mias")
    @PreAuthorize("hasRole('SOCIO') or hasRole('ADMIN') or hasRole('PROFESOR')")
    public ResponseEntity<List<AsistenciaResponse>> listarMias(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<AsistenciaResponse> data = asistenciaSocioService.listarPorUsuarioAutenticado(desde, hasta);
        return ResponseEntity.ok(data);
    }
}
