package com.romacontrol.romacontrol_v1.dto.rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mostrar permisos asociados a un rol.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoRolRespuesta {

    private Long idPermiso;
    private String ruta;
    private String descripcion;
    private boolean activoPermiso;
    private String titulo;  
    private boolean habilitadoEnRol;
}
