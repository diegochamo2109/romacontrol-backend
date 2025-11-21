package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPagoResponse {

    // 🔹 Fecha y hora del pago (zona Argentina)
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        timezone = "America/Argentina/Buenos_Aires"
    )
    private LocalDateTime fechaPago;

    private String dni;
    private String nombreCompleto;
    private String cuotaDescripcion;

    // 🔹 Fecha de vencimiento de la cuota (zona Argentina)
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        timezone = "America/Argentina/Buenos_Aires"
    )
    private LocalDateTime fechaVencimiento;

    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private Boolean conRetraso;
    private String cobradoPor;
    private String observacion;
}
