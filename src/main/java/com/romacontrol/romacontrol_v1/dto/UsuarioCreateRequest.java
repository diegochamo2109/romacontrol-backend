package com.romacontrol.romacontrol_v1.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioCreateRequest(
    @NotBlank String dni,
    @NotBlank String pin,            // lo guardamos hasheado
        @NotNull Long cuotaMensualId, 
    @NotNull  List<Long> rolIds,     // ids de roles tildados en la UI
    @NotNull  PersonaDTO persona,
    @NotNull  ContactoUrgenciaDTO contacto
) {}
