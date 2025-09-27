package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Ítem liviano para la grilla de cuotas de un usuario. */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioCuotaItemResponse {
  private Long cuotaId;
  private String descripcion;
  private String estado;          // PENDIENTE / VENCIDA / PAGADA
  private boolean conRetraso;
  private String fechaLimiteIso;  // ISO-8601 para frontend
  private BigDecimal importe;
}
