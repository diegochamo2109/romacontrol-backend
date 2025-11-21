

package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PagoResponse {
    private Long id;
    private Long usuarioId;
    private Long cuotaMensualId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private OffsetDateTime fechaPago;
    private String cobradoPor;
    private boolean fueraDeTermino;
    private LocalDate fechaVencimiento;
   

}
