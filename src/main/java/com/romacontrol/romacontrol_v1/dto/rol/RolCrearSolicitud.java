package com.romacontrol.romacontrol_v1.dto.rol;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolCrearSolicitud {

    @NotBlank
    private String nombre;

    private String descripcion;

    /** Lista de IDs de permisos a asociar (opcional). */
    private List<Long> permisosIds;
}
