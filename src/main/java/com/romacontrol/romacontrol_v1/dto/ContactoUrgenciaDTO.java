

package com.romacontrol.romacontrol_v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactoUrgenciaDTO(
    @NotBlank String nombre,
    @NotBlank String apellido,
    @NotBlank String telefonoArea,
    @NotBlank String telefonoNumero,
    @NotBlank String relacion,
    @NotNull Long localidadId
) {}
