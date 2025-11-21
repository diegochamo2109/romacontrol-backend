package com.romacontrol.romacontrol_v1.dto.socio;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el historial de pagos del socio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoSocioRespuesta {

    private Long idPago;
    private Long cuotaMensualId;
    private String descripcionCuota;

    private BigDecimal montoPagado;
    private OffsetDateTime fechaPago;

    private String metodoPago;  // EFECTIVO, TARJETA, TRANSFERENCIA, etc.
    private String estadoPago;  // PAGADO, PAGADO_CON_RETRASO, etc.
    private String cobradoPor;
}
