package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaListadoDto {

    private Long id;
    private String descripcion;
    private BigDecimal importe;
    private OffsetDateTime fechaAlta;
    private OffsetDateTime fechaLimite;

    private String estado;     // PROGRAMADA / CUOTA_DEL_MES / etc.
    private String tipo;       // Mensual, etc.
}
