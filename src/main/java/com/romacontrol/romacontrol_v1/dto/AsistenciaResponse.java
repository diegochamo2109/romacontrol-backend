package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaResponse {
    private Long id;
    private String nombreCompleto;
    private String dni;
    private LocalDateTime fechaHora;
    private String estado;
    private String observacion;
}
