package com.romacontrol.romacontrol_v1.dto.cierre;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mostrar el historial de cierres completos en una tabla.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreCompletoHistorialResponse {

    private Long id;                 // ID del cierre completo
    private String fechaHoraCierre;  // Formato: 16-11-2025 | 11:04
    private BigDecimal totalGeneral; // Total recaudado en ese cierre
    private String cerradoPor;       // Nombre y apellido del ADMIN que cerró
}