package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que recibe los datos desde el frontend para crear un nuevo gasto.
 * Se utiliza en el endpoint POST /api/gastos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreacionGastoSolicitud {

    /** ID del tipo de gasto seleccionado */
    @NotNull(message = "Debe seleccionar un tipo de gasto.")
    private Long tipoGastoId;

    /** Monto total del gasto */
    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0.")
    private BigDecimal monto;

    /** Observaciones o detalles del gasto (opcional) */
    private String descripcion;

    // 🔹 Este DTO se mantiene simple. 
    // No se incluye 'fecha' ni 'activo' porque:
    // - La fecha se asigna automáticamente (OffsetDateTime Buenos Aires) en la entidad Gasto.
    // - El campo 'activo' siempre inicia como TRUE al crearse el registro.
}
