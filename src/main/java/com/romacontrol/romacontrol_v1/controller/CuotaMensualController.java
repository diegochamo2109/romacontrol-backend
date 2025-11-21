package com.romacontrol.romacontrol_v1.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.romacontrol.romacontrol_v1.dto.CreacionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.dto.EdicionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.service.CuotaMensualService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuotas")
@RequiredArgsConstructor
public class CuotaMensualController {

    private final CuotaMensualService cuotaService;

    // ===========================================================
    // 1) CREAR NUEVA CUOTA
    // ===========================================================
    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody CreacionCuotaSolicitud dto,
            BindingResult result,
            @RequestHeader("dni") String dniCreador) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", result.getFieldError().getDefaultMessage()));
        }

        try {
            return ResponseEntity.ok(cuotaService.crearCuota(dto, dniCreador));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ===========================================================
    // 2) LISTADO COMPLETO
    // ===========================================================
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(cuotaService.listar());
    }

    // ===========================================================
    // 3) DETALLE DE CUOTA
    // ===========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cuotaService.detalle(id));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ===========================================================
    // 4) EDITAR CUOTA PROGRAMADA
    // ===========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @PathVariable Long id,
            @Valid @RequestBody EdicionCuotaSolicitud dto,
            BindingResult result,
            @RequestHeader("dni") String dniEditor) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", result.getFieldError().getDefaultMessage()));
        }

        try {
            return ResponseEntity.ok(cuotaService.editar(id, dto, dniEditor));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ===========================================================
    // 5) ELIMINAR CUOTA (solo si no tiene pagos)
    // ===========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            cuotaService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Cuota eliminada correctamente."));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ===========================================================
    // 6) CAMBIAR ESTADO DE CUOTA (Manual)
    // ===========================================================
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @PathVariable String nuevoEstado,
            @RequestHeader("dni") String dniAdmin) {
        try {
            return ResponseEntity.ok(cuotaService.cambiarEstado(id, nuevoEstado, dniAdmin));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ===========================================================
    // 7) OBTENER CUOTA DEL MES
    // ===========================================================
    @GetMapping("/del-mes")
    public ResponseEntity<?> cuotaDelMes() {
        try {
            return ResponseEntity.ok(cuotaService.obtenerCuotaDelMes());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
