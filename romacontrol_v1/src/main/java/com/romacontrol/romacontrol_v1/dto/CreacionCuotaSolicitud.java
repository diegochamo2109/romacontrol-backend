package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para crear una nueva cuota desde el frontend básico.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreacionCuotaSolicitud {

    @NotNull(message = "La descripción es obligatoria.")
    private String descripcion;

    @NotNull(message = "El importe es obligatorio.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El importe debe ser mayor a cero.")
    private BigDecimal importe;

    @NotNull(message = "La fecha de activación es obligatoria.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de vencimiento es obligatoria.")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El tipo de cuota es obligatorio.")
    private Long tipoCuotaId;

    private boolean asignar = false;
}
