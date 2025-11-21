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
public class SocioPagoResponse {
    private Long id;
    private String descripcionCuota;
    private Double importe;
    private String metodoPago;
    private String estado;
    private OffsetDateTime fechaPago;
    private String registradoPor;
}
