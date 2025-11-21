package com.romacontrol.romacontrol_v1.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.CierreCompletoResponse;
import com.romacontrol.romacontrol_v1.dto.CierreParcialResponse;
import com.romacontrol.romacontrol_v1.service.CajaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    // 🔹 Cierre Parcial: pagos del usuario logueado (ADMIN o PROFESOR)
    @GetMapping("/cierre-parcial")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
    public ResponseEntity<List<CierreParcialResponse>> obtenerCierreParcial(
            @RequestParam(required = false) String fecha) {
        LocalDate fechaFiltro = (fecha != null) ? LocalDate.parse(fecha) : LocalDate.now();
        return ResponseEntity.ok(cajaService.obtenerCierreParcial(fechaFiltro));
    }

    // 🔹 Cierre Completo: solo ADMIN
    @GetMapping("/cierre-completo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CierreCompletoResponse>> obtenerCierreCompleto(
            @RequestParam(required = false) String fecha) {
        LocalDate fechaFiltro = (fecha != null) ? LocalDate.parse(fecha) : LocalDate.now();
        return ResponseEntity.ok(cajaService.obtenerCierreCompleto(fechaFiltro));
    }
}
