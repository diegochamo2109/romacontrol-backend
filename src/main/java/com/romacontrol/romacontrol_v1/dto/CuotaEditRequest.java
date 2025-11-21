package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload para editar una cuota existente.
 * Compatible con el frontend (gestionar-cuotas.js)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaEditRequest {

  @NotNull(message = "El ID de la cuota es obligatorio")
  private Long id;

  @NotBlank(message = "La descripción es obligatoria")
  private String descripcion;

  @NotNull(message = "El importe es obligatorio")
  @DecimalMin(value = "0.01", message = "El importe debe ser mayor a cero")
  private BigDecimal importe;

  @NotNull(message = "La fecha límite es obligatoria")
  @FutureOrPresent(message = "La fecha no puede ser pasada")
  private LocalDate fechaLimite;  // 👈 coincide con el campo del backend y el JSON del JS

  private boolean activa; // opcional (para futuros cambios)

  // 🔥 NUEVO
  private boolean cuotaDelMes;
}
