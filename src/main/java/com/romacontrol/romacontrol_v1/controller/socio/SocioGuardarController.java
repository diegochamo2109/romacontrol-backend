package com.romacontrol.romacontrol_v1.controller.socio;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.socio.MisDatosSocioUpdateRequest;
import com.romacontrol.romacontrol_v1.service.socio.SocioGuardarService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/socio/guardar")
@RequiredArgsConstructor
public class SocioGuardarController {

    private final SocioGuardarService socioGuardarService;

    // ============================================================
    // 💾 GUARDAR/ACTUALIZAR INFORMACIÓN DEL SOCIO
    // ============================================================
    @PutMapping("/mis-datos")
    @Transactional
    public void actualizarMisDatos(@Valid @RequestBody MisDatosSocioUpdateRequest request) {
        socioGuardarService.actualizarMisDatos(request);
    }
}
