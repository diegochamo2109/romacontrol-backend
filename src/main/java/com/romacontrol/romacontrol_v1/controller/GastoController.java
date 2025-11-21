package com.romacontrol.romacontrol_v1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.CreacionGastoSolicitud;
import com.romacontrol.romacontrol_v1.dto.GastoEdicionSolicitud;
import com.romacontrol.romacontrol_v1.dto.GastoListadoRespuesta;
import com.romacontrol.romacontrol_v1.dto.GastoRespuesta;
import com.romacontrol.romacontrol_v1.service.GastoService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de gastos.
 * Módulo: "Registrar / Gestionar Gastos"
 * 
 * Permite:
 *  - Crear nuevos gastos.
 *  - Listar todos o solo los activos.
 *  - Editar tipo, monto y descripción.
 *  - Eliminar lógicamente (activar/desactivar).
 */
@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    // =====================================================
    // 🔹 CREAR GASTO
    // =====================================================
    @PostMapping
    public ResponseEntity<GastoRespuesta> crearGasto(
            @RequestBody CreacionGastoSolicitud solicitud,
            Authentication authentication) {

        String dniUsuario = authentication.getName(); // viene del JWT
        GastoRespuesta respuesta = gastoService.crearGasto(solicitud, dniUsuario);
        return ResponseEntity.ok(respuesta);
    }

    // =====================================================
    // 🔹 LISTAR GASTOS (para historial simple)
    // =====================================================
    @GetMapping
    public ResponseEntity<List<GastoRespuesta>> listarTodos() {
        return ResponseEntity.ok(gastoService.listarTodos());
    }

    // =====================================================
    // 🔹 LISTAR GASTOS PARA GESTIÓN (activos e inactivos)
    // =====================================================
    @GetMapping("/gestion")
    public ResponseEntity<List<GastoListadoRespuesta>> listarGestion() {
        return ResponseEntity.ok(gastoService.listarParaGestion());
    }

    // =====================================================
    // 🔹 EDITAR GASTO EXISTENTE
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<GastoListadoRespuesta> editarGasto(
            @PathVariable Long id,
            @RequestBody GastoEdicionSolicitud solicitud,
            Authentication authentication) {

        String dniUsuario = authentication.getName();
        GastoListadoRespuesta respuesta = gastoService.editar(id, solicitud, dniUsuario);
        return ResponseEntity.ok(respuesta);
    }

    // =====================================================
    // 🔹 CAMBIO DE ESTADO (ELIMINACIÓN LÓGICA)
    // =====================================================
    @PatchMapping("/{id}/activo")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo,
            Authentication authentication) {

        String dniUsuario = authentication.getName();
        gastoService.cambiarActivo(id, activo, dniUsuario);
        return ResponseEntity.noContent().build();
    }
}
