package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para devolver la información de una cuota.
 * Se utiliza tanto al crear una nueva cuota como al listarlas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaDetailResponse {

  private Long id;

  private String descripcion;

  private BigDecimal importe;

  /** Fecha en que se dio de alta la cuota */
  private OffsetDateTime fechaAlta;

  /** Fecha límite de pago de la cuota */
  private OffsetDateTime fechaLimite;

  /** Estado actual de la cuota (nombre de EstadoCuota: ACTIVA / INACTIVA) */
  private String estado;

  /** Tipo de cuota (nombre de TipoCuota: Mensual, Semanal, etc.) */
  private String tipo;

  /** Indica si la cuota está marcada como activa */
  private boolean activa;

  /** Cantidad de usuarios a los que se les asignó esta cuota */
  private int totalAsignadas;
}
