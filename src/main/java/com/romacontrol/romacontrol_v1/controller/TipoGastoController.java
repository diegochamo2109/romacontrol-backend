package com.romacontrol.romacontrol_v1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.CreacionTipoGastoSolicitud;
import com.romacontrol.romacontrol_v1.dto.TipoGastoRespuesta;
import com.romacontrol.romacontrol_v1.service.TipoGastoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipo-gasto")
@RequiredArgsConstructor
public class TipoGastoController {

    private final TipoGastoService tipoGastoService;

    // 🔹 Crear nuevo tipo de gasto
    @PostMapping
    public ResponseEntity<TipoGastoRespuesta> crearTipoGasto(
            @RequestBody CreacionTipoGastoSolicitud solicitud) {
        TipoGastoRespuesta nuevo = tipoGastoService.crearTipoGasto(solicitud);
        return ResponseEntity.ok(nuevo);
    }

    // 🔹 Listar todos los tipos de gasto
    @GetMapping
    public ResponseEntity<List<TipoGastoRespuesta>> listarTodos() {
        return ResponseEntity.ok(tipoGastoService.listarTodos());
    }
}
