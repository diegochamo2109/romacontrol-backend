package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta al registrar un pago. */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PagoDetailResponse {
  private Long id;
  private String dni;
  private String usuarioNombre;     // Nombre y apellido
  private Long cuotaMensualId;
  private String cuotaDescripcion;
  private OffsetDateTime fechaPago;
  private BigDecimal monto;
  private boolean fueraDeTermino;
  private String metodoPago;        // nombre del método
}
