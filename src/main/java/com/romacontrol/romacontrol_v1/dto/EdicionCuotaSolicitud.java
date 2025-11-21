    package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EdicionCuotaSolicitud {

    @NotNull
    private BigDecimal importe;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaVencimiento;

    @NotNull
    private String descripcion;
}
