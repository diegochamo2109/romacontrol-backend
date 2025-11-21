package com.romacontrol.romacontrol_v1.dto.cierre;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CierreParcialRequest {

    private Long usuarioId;   // ID del profesor o admin que está cerrando caja
    private LocalDate fecha;  // Día que se quiere cerrar (ej: hoy)


}
