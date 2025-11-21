package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RegistroPagoSolicitud {

    private Long usuarioId;       // Alumno que paga
    private Long cuotaMensualId;  // Cuota que está pagando
    private Long metodoPagoId;    // Efectivo, Tarjeta, etc.
    private BigDecimal monto;     // Monto abonado

    // 🆕 Fecha real del pago (OPCIONAL)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPagoManual;
}
