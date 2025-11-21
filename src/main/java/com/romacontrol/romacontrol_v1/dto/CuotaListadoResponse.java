package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO resumido para listar cuotas en la pantalla de gestión.
 * Incluye información básica, fechas y contadores.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaListadoResponse {

    private Long id;
    private String descripcion;
    private BigDecimal importe;
    private OffsetDateTime fechaAlta;
    private OffsetDateTime fechaLimite;
    private String estado;
    private String tipo;

    private Long totalAsignados;
    private Long pendientes;
    private Long pagadas;
    private Long vencidas;
}
