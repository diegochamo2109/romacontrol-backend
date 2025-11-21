package com.romacontrol.romacontrol_v1.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.HistorialPagoResponse;
import com.romacontrol.romacontrol_v1.dto.ResumenCuotaResponse;
import com.romacontrol.romacontrol_v1.service.ReporteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/historial-pagos")
    public ResponseEntity<List<HistorialPagoResponse>> obtenerHistorialPagos(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(reporteService.obtenerHistorialPagos(dni, desde, hasta, estado));
    }
    @GetMapping("/historial-pagos-completo")
public ResponseEntity<List<HistorialPagoResponse>> obtenerHistorialPagosCompleto(
        @RequestParam(required = false) String dni,
        @RequestParam(required = false) String desde,
        @RequestParam(required = false) String hasta,
        @RequestParam(required = false) String estado) {

    return ResponseEntity.ok(
        reporteService.obtenerHistorialPagosCompleto(dni, desde, hasta, estado)
    );
}


    @GetMapping("/resumen-cuotas")
    public ResponseEntity<List<ResumenCuotaResponse>> obtenerResumenCuotas() {
        return ResponseEntity.ok(reporteService.obtenerResumenCuotas());
    }
}
