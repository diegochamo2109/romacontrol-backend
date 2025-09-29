package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioCuotaDTO {
    private Long id;
    private Long cuotaId;
    private String descripcion;
    private BigDecimal importe;
    private UsuarioCuotaEstado estado;
    private OffsetDateTime fechaAsignacion;
}
