package com.romacontrol.romacontrol_v1.controller.cierre;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.CierreCompletoResponse;
import com.romacontrol.romacontrol_v1.dto.cierre.CierreCompletoSolicitud;
import com.romacontrol.romacontrol_v1.service.CajaCierreService;
import com.romacontrol.romacontrol_v1.service.CajaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cierre-completo")
@RequiredArgsConstructor
public class CierreCompletoController {

    private final CajaService cajaService;             // EXISTENTE (trae resumen)
    private final CajaCierreService cajaCierreService; // NUEVO (registra cierre real)

    // ============================================================
    // 🔹 1) Obtener resumen completo (EXISTENTE, NO TOCAR)
    // ============================================================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CierreCompletoResponse>> obtenerCierreCompleto(
            @RequestParam(required = false) String fecha) {

        LocalDate fechaFiltro = (fecha != null)
                ? LocalDate.parse(fecha)
                : LocalDate.now();

        return ResponseEntity.ok(cajaService.obtenerCierreCompleto(fechaFiltro));
    }

    // ============================================================
    // ⭐ 2) Registrar cierre completo REAL (NUEVO con JSON)
    // ============================================================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarCierreCompleto(
            @RequestBody CierreCompletoSolicitud solicitud) {

        LocalDate fechaFiltro = (solicitud.getFecha() != null)
                ? LocalDate.parse(solicitud.getFecha())
                : LocalDate.now();

        cajaCierreService.registrarCierreCompleto(fechaFiltro);

        return ResponseEntity.ok(
                Map.of("mensaje", "Cierre completo registrado correctamente.")
        );
    }
}
    