package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que devuelve los datos al frontend tras crear o listar un gasto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoRespuesta {

    private Long id;
    private String tipoGastoNombre;
    private BigDecimal monto;
    private String descripcion;
    private String registradoPorNombre;
    private String fechaFormateada; // 🔹 Fecha lista para mostrar (dd/MM/yyyy HH:mm)
}
