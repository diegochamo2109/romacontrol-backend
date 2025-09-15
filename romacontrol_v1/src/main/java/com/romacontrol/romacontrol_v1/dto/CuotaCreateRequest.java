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
 * Payload para crear una nueva cuota mensual.
 * El estado y si queda activa o no se determinan en el service
 * según el flag 'asignar' que llega como @RequestParam.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaCreateRequest {

  @NotBlank(message = "La descripción es obligatoria")
  private String descripcion;

  @NotNull(message = "El importe es obligatorio")
  @DecimalMin(value = "0.01", inclusive = true, message = "El importe debe ser mayor a cero")
  private BigDecimal importe;

  @NotNull(message = "La fecha de vencimiento es obligatoria")
  @FutureOrPresent(message = "La fecha de vencimiento no puede ser pasada")
  private LocalDate fechaVencimiento;

  @NotNull(message = "El tipo de cuota es obligatorio")
  private Long tipoCuotaId;
}
