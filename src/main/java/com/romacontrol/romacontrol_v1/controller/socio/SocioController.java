package com.romacontrol.romacontrol_v1.controller.socio;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.socio.AsistenciaSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.CuotaSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.MisDatosSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.PagoSocioRespuesta;
import com.romacontrol.romacontrol_v1.service.socio.SocioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/socio")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    // ============================================================
    // 👤 1) MIS DATOS
    // ============================================================
    @GetMapping("/mis-datos")
    @Transactional(readOnly = true)
    public MisDatosSocioRespuesta obtenerMisDatos() {
        return socioService.obtenerMisDatos();
    }

    


    // ============================================================
    // 💰 2) MIS CUOTAS (pendientes y vencidas)
    // ============================================================
    @GetMapping("/mis-cuotas")
    @Transactional(readOnly = true)
    public List<CuotaSocioRespuesta> obtenerMisCuotas() {
        return socioService.obtenerMisCuotasPendientesYVencidas();
    }

    // ============================================================
    // 💸 3) MIS PAGOS
    // ============================================================
    @GetMapping("/mis-pagos")
    @Transactional(readOnly = true)
    public List<PagoSocioRespuesta> obtenerMisPagos() {
        return socioService.obtenerMisPagos();
    }

    // ============================================================
    // 📅 4) MIS ASISTENCIAS
    // ============================================================
    @GetMapping("/mis-asistencias")
    @Transactional(readOnly = true)
    public List<AsistenciaSocioRespuesta> obtenerMisAsistencias() {
        return socioService.obtenerMisAsistencias();
    }
}
