package com.romacontrol.romacontrol_v1.controller.rol;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.rol.PermisoCrearSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.PermisoEditarSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.PermisoRespuesta;
import com.romacontrol.romacontrol_v1.service.rol.PermisoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/permisos")
@RequiredArgsConstructor
@Validated
public class PermisoController {

    private final PermisoService permisoService;

    @PostMapping
    public ResponseEntity<PermisoRespuesta> crear(@RequestBody @Validated PermisoCrearSolicitud solicitud) {
        return ResponseEntity.ok(permisoService.crear(solicitud));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermisoRespuesta> editar(
            @PathVariable Long id,
            @RequestBody @Validated PermisoEditarSolicitud solicitud) {

        return ResponseEntity.ok(permisoService.editar(id, solicitud));
    }

    @GetMapping
    public ResponseEntity<List<PermisoRespuesta>> listar() {
        return ResponseEntity.ok(permisoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermisoRespuesta> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(permisoService.obtenerPorId(id));
    }
}
