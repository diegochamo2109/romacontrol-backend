package com.romacontrol.romacontrol_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolPermisoResponse {
    private Long id;
    private String nombreModulo;
    private String ruta;
    private boolean activo;
}
