package com.romacontrol.romacontrol_v1.dto;

public record UsuarioResponse(
    Long id,
    String dni,
    String nombre,
    String apellido
) {}
