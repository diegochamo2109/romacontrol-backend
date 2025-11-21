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
public class UsuarioCuotaItemResponse {

    private Long usuarioId;
    private String dni;
    private String nombreCompleto;

    private Long cuotaId;
    private String descripcionCuota;
    private BigDecimal importe;
    private String estado;            // PENDIENTE | PAGADA
    private boolean conRetraso;

    private OffsetDateTime fechaAsignacion;
    private OffsetDateTime fechaCambioEstado; // cuándo pasó a PAGADA
    private OffsetDateTime fechaLimite;       // límite de la cuota
}
