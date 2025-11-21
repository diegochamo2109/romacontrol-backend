package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que recibe los datos desde el frontend para editar un gasto existente.
 * Se utiliza en el endpoint PUT /api/gastos/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoEdicionSolicitud {

    /** ID del tipo de gasto actualizado */
    @NotNull(message = "Debe seleccionar un tipo de gasto.")
    private Long tipoGastoId;

    /** Monto actualizado del gasto */
    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0.")
    private BigDecimal monto;

    /** Nueva descripción u observaciones (opcional) */
    private String descripcion;
}
