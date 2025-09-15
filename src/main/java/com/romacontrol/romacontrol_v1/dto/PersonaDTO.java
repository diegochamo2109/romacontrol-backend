



package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonaDTO(
    @NotBlank String nombre,
    @NotBlank String apellido,
    @NotNull  LocalDate fechaNacimiento,   // enviar ISO: yyyy-MM-dd
    @NotBlank String domicilio,
    @NotBlank String telefonoArea,
    @NotBlank String telefonoNumero,
    @Email @NotBlank String email,
    @NotNull Long generoId,
    @NotNull Long localidadId
) {}
