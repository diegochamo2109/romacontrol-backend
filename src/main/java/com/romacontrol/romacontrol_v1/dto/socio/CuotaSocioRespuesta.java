package com.romacontrol.romacontrol_v1.dto.socio;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para listar las cuotas del socio (pendientes, vencidas o cerradas).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaSocioRespuesta {

    private Long idUsuarioCuota;
    private Long cuotaMensualId;

    private String descripcionCuota;
    private BigDecimal importe;
    private LocalDate fechaLimite;

    // Texto del estado: "PENDIENTE", "VENCIDA", "CERRADA"
    private String estadoCuota;

    // Flags para lógica del frontend
    private boolean pendiente;
    private boolean vencida;
    private boolean cerrada;
}
