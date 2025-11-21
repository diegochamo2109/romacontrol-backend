package com.romacontrol.romacontrol_v1.dto.socio;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el historial de asistencias del socio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaSocioRespuesta {

    private Long idAsistencia;
    private OffsetDateTime fechaHora;
    private String tipo;      // ENTRADA, SALIDA, etc. según tu dominio
    private String observacion; // opcional
}
