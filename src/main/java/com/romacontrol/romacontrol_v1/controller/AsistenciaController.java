package com.romacontrol.romacontrol_v1.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.AsistenciaResponse;
import com.romacontrol.romacontrol_v1.service.AsistenciaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    /**
     * 🔹 Registrar asistencia desde la terminal (ya funcional)
     */
    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarAsistencia(@RequestBody Map<String, String> request) {
        String dni = request.get("dni");
        String pin = request.get("pin");
        Map<String, Object> respuesta = asistenciaService.registrarAsistencia(dni, pin);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * 📋 Listar todas las asistencias (para el panel de administración)
     */
    @GetMapping("/listar")
    public ResponseEntity<List<AsistenciaResponse>> listarAsistencias() {
        return ResponseEntity.ok(asistenciaService.listarAsistencias());
    }

    
}
