package com.romacontrol.romacontrol_v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreacionTipoGastoSolicitud {

    @NotBlank(message = "El nombre del tipo de gasto es obligatorio.")
    private String nombre;

    private String descripcion;  // Opcional
}
