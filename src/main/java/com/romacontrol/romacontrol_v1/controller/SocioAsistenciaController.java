package com.romacontrol.romacontrol_v1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.SocioAsistenciaResponse;
import com.romacontrol.romacontrol_v1.service.SocioAsistenciaService;

import lombok.RequiredArgsConstructor;

/**
 * 🔹 Controlador para el módulo "Mis Asistencias" del socio.
 * Endpoint: /api/socio/asistencias?dni=33548166
 */
@RestController
@RequestMapping("/api/socio/asistencias")
@RequiredArgsConstructor
public class SocioAsistenciaController {

    private final SocioAsistenciaService socioAsistenciaService;

    /**
     * 📋 Devuelve todas las asistencias del socio autenticado (por DNI).
     */
    @GetMapping
    public ResponseEntity<List<SocioAsistenciaResponse>> listar(@RequestParam String dni) {
        return ResponseEntity.ok(socioAsistenciaService.listarMisAsistencias(dni));
    }
}
