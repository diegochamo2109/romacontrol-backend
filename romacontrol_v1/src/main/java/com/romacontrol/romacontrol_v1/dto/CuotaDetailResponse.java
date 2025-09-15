package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta al crear/listar cuota. */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CuotaDetailResponse {
  private Long id;
  private String descripcion;
  private BigDecimal importe;
  private OffsetDateTime fechaAlta;
  private OffsetDateTime fechaLimite;
  private String estado;     // nombre de EstadoCuota
  private String tipo;       // nombre de TipoCuota
  private boolean activa;
  private int totalAsignadas; // cantidad de UsuarioCuota creadas (si corresponde)
}
