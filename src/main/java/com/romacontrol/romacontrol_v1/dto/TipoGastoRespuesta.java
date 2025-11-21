package com.romacontrol.romacontrol_v1.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoGastoRespuesta {
    private Long id;
    private String nombre;
    private String descripcion;
    private OffsetDateTime fechaCreacion;
}
