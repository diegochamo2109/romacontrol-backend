package com.romacontrol.romacontrol_v1.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de creación de usuario.
 * - cuotaMensualId: ahora puede ser null (Admin/Profesor). La regla de obligatoriedad
 *   cuando tenga rol "Socio" se valida en el servicio.
 * - rolIds: @NotEmpty para exigir al menos un rol.
 * - persona/contacto: @Valid para ejecutar validaciones internas de esos DTOs.
 */
public record UsuarioCreateRequest(
    @NotBlank String dni,
    @NotBlank String pin,           // se almacenará hasheado
    Long cuotaMensualId,            // <-- AHORA opcional (null permitido si no es Socio)
    @NotEmpty List<Long> rolIds,    // <-- al menos un rol
    @NotNull @Valid PersonaDTO persona,
    @NotNull @Valid ContactoUrgenciaDTO contacto
) {}
