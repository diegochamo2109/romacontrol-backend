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
 * El estado (ACTIVA/INACTIVA) y si se asigna automáticamente
 * a los usuarios activos se determinan en el Service,
 * en base al flag 'asignar' recibido como @RequestParam en el Controller.
 *
 * 👉 El tipo de cuota no se envía más desde el front;
 * siempre se fija automáticamente en "Mensual" dentro del Service.
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
  @FutureOrPresent(message = "Selecciona una fecha válida: no puede ser menor al día actual.")

  private LocalDate fechaVencimiento;

    // 🔥 NUEVO
  private boolean cuotaDelMes;
}
