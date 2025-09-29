package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RegistroPagoSolicitud {
    private Long usuarioId;       // Alumno que paga
    private Long cuotaMensualId;  // Cuota que está pagando
    private Long metodoPagoId;    // Efectivo, Tarjeta, etc.
    private BigDecimal monto;     // Monto abonado (debe ser igual al importe de la cuota)
}
