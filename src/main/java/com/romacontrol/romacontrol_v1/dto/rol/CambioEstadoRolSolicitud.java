package com.romacontrol.romacontrol_v1.dto.rol;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEstadoRolSolicitud {

    @NotNull
    private Boolean activo;
}
