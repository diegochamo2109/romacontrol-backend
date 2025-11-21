package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenCuotaResponse {
    private String descripcionCuota;
    private BigDecimal importe;
    private Long totalUsuariosAsignados;
    private Long totalPagosRealizados;
    private Long pagosConRetraso;
    private BigDecimal totalRecaudado;
}
