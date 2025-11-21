package com.romacontrol.romacontrol_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para activar o desactivar una cuota existente.
 * Usado desde el módulo "Gestionar Cuotas".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoCuotaSolicitud {
    private boolean activa;         // true = activar, false = desactivar
    private String modificadoPor;   // DNI o username del usuario que realiza el cambio
}
