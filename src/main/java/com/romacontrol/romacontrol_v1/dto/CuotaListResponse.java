package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO alternativo (versión anterior o para listados internos).
 * No se usa en el frontend actual, pero se conserva para compatibilidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuotaListResponse {

    private Long id;
    private String descripcion;
    private BigDecimal importe;
    private OffsetDateTime fechaAlta;
    private OffsetDateTime fechaLimite;
    private String creadoPor;
    private String estado;
    private boolean activa;
}
