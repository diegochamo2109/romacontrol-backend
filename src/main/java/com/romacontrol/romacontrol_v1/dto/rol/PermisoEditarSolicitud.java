package com.romacontrol.romacontrol_v1.dto.rol;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermisoEditarSolicitud {

    @NotBlank
    private String ruta;
    private String titulo;  

    private String descripcion;

    private Boolean activo;
}
