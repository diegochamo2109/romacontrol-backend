package com.romacontrol.romacontrol_v1.dto.rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoRespuesta {

    private Long id;
    private String ruta;
    private String descripcion;
    private String titulo;
    private boolean activo;
}
