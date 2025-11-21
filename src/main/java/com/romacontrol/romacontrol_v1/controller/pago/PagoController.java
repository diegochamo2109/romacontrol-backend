package com.romacontrol.romacontrol_v1.controller.pago;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.PagoResponse;
import com.romacontrol.romacontrol_v1.dto.RegistroPagoSolicitud;
import com.romacontrol.romacontrol_v1.service.PagoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponse> registrarPago(
            @RequestBody RegistroPagoSolicitud solicitud,
            Authentication authentication) {

        // 🟢 Usuario que está registrando el pago (ADMIN o PROFESOR)
        String usuarioLogueado = authentication.getName();

        // 🟢 Delegamos en el servicio (ya implementado ANTES)
        PagoResponse pago = pagoService.registrarPago(solicitud, usuarioLogueado);

        return ResponseEntity.ok(pago);
    }
}
