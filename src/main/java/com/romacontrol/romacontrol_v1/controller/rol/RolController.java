

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

import com.romacontrol.romacontrol_v1.dto.rol.CambioEstadoRolSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolCrearSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolDetalleRespuesta;
import com.romacontrol.romacontrol_v1.dto.rol.RolEditarSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolListadoRespuesta;
import com.romacontrol.romacontrol_v1.service.rol.RolService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
public class RolController {

    private final RolService rolService;

    @PostMapping
    public ResponseEntity<RolDetalleRespuesta> crear(@RequestBody @Validated RolCrearSolicitud solicitud) {
        return ResponseEntity.ok(rolService.crear(solicitud));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDetalleRespuesta> editar(
            @PathVariable Long id,
            @RequestBody @Validated RolEditarSolicitud solicitud) {
        return ResponseEntity.ok(rolService.editar(id, solicitud));
    }

    @PutMapping("/{id}/cambiar-estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestBody @Validated CambioEstadoRolSolicitud solicitud) {

        rolService.cambiarEstado(id, solicitud);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RolListadoRespuesta>> listar() {
        return ResponseEntity.ok(rolService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDetalleRespuesta> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.obtenerDetalle(id));
    }
}
