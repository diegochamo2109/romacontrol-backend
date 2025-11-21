package com.romacontrol.romacontrol_v1.controller.cierre;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.cierre.CierreParcialRequest;
import com.romacontrol.romacontrol_v1.dto.cierre.CierreParcialResponse;
import com.romacontrol.romacontrol_v1.service.cierre.CierreParcialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cierre-parcial")
@RequiredArgsConstructor
public class CierreParcialController {

    private final CierreParcialService cierreParcialService;

    // ============================================================
    // 1) Obtener cierre del usuario logueado
    // ============================================================
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public ResponseEntity<List<CierreParcialResponse>> obtenerCierre(
            @RequestParam(required = false) String fecha,
            @RequestParam Long usuarioId) {

        LocalDate fechaFiltro = (fecha != null) ? LocalDate.parse(fecha) : LocalDate.now();

        var resultado = cierreParcialService.obtenerCierreDeUsuario(fechaFiltro, usuarioId);
        return ResponseEntity.ok(resultado);
    }

    // ============================================================
    // 2) ADMIN: obtener todos los cierres de una fecha
    // ============================================================
    @GetMapping("/fecha")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CierreParcialResponse>> obtenerCierresFecha(
            @RequestParam(required = false) String fecha) {

        LocalDate fechaFiltro = (fecha != null) ? LocalDate.parse(fecha) : LocalDate.now();
        return ResponseEntity.ok(cierreParcialService.obtenerCierresDeFecha(fechaFiltro));
    }

    // ============================================================
    // 3) Registrar cierre parcial
    // ============================================================
    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public ResponseEntity<CierreParcialResponse> generarCierre(
            @RequestBody CierreParcialRequest request) {

        return ResponseEntity.ok(cierreParcialService.generarCierre(request));
    }
}
