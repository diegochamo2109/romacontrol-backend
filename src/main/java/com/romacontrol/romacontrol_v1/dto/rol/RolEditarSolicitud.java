package com.romacontrol.romacontrol_v1.dto.rol;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolEditarSolicitud {

    @NotBlank
    private String nombre;

    private String descripcion;

    private Boolean activo;

    /** Lista completa de permisos que quedarán asociados al rol. */
    private List<Long> permisosIds;
}
