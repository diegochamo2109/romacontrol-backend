package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un gasto para mostrarlo en el listado
 * del módulo "Gestionar Gastos".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoListadoRespuesta {

    /** ID único del gasto */
    private Long id;

    /** Nombre del tipo de gasto (ej: Luz, Agua, Sueldos, etc.) */
    private String tipoGastoNombre;

    /** ID del tipo de gasto (para edición) */
    private Long tipoGastoId;

    /** Monto total del gasto */
    private BigDecimal monto;

    /** Descripción u observaciones (opcional) */
    private String descripcion;

    /** Nombre completo del usuario que registró el gasto */
    private String registradoPorNombre;

    /** Fecha formateada (dd/MM/yyyy HH:mm) */
    private String fechaFormateada;

    /** Estado lógico del gasto (activo = true / eliminado = false) */
    private Boolean activo;
}
