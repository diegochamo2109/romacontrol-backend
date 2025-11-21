package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocioAsistenciaResponse {
    private Long id;
    private LocalDate fechaRegistro;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private boolean salidaGeneradaAutomatica;
    private String observacion;
}
