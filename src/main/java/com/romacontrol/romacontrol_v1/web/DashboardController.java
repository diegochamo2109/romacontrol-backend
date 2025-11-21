package com.romacontrol.romacontrol_v1.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.respuesta.DashboardResponse;
import com.romacontrol.romacontrol_v1.dto.respuesta.PagoDiaResponse;
import com.romacontrol.romacontrol_v1.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // o especificá tu frontend si querés restringirlo
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Endpoint del panel de control (solo ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardResponse> obtenerDatosDashboard() {
        DashboardResponse datos = dashboardService.obtenerDatosDashboard();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/pagos-por-dia")
public ResponseEntity<List<PagoDiaResponse>> pagosPorDia(
        @RequestParam int mes,
        @RequestParam int anio) {

    return ResponseEntity.ok(dashboardService.obtenerPagosPorDia(mes, anio));
}

    /**
     * Versión pública opcional (para pruebas sin token)
     * ⚠️ Podés comentarla o eliminarla en producción
     */
    @GetMapping("/publico")
    public ResponseEntity<DashboardResponse> obtenerDatosDashboardPublico() {
        DashboardResponse datos = dashboardService.obtenerDatosDashboard();
        return ResponseEntity.ok(datos);
    }
}
