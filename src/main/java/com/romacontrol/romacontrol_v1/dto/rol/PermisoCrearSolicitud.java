package com.romacontrol.romacontrol_v1.dto.rol;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermisoCrearSolicitud {

    @NotBlank
    private String ruta;
     @NotBlank
    private String titulo;
    private String descripcion;
}
