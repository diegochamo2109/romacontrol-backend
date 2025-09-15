package com.romacontrol.romacontrol_v1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registrar un pago para una cuota asignada a un usuario.
 * Se puede enviar 'dni' o 'usuarioId' (el service resuelve).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoCreateRequest {

  // Alternativa 1: buscar usuario por DNI
  private String dni;

  // Alternativa 2: pasar ID del usuario
  private Long usuarioId;

  // ID de la cuota mensual asignada
  @NotNull(message = "Debe especificar la cuota mensual")
  private Long cuotaMensualId;

  // ID del método de pago (efectivo, transferencia, etc.)
  @NotNull(message = "Debe especificar el método de pago")
  private Long metodoPagoId;
}
