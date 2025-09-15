package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Payload para editar/actualizar un usuario existente */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {

  /** Si viene con 4 dígitos, se actualiza el PIN (se encripta en el service) */
  @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe tener 4 dígitos")
  private String pin;

  @NotNull
  private PersonaDTO persona;

  @NotNull
  private ContactoUrgenciaDTO contacto;

  @NotNull
  private List<Long> rolIds;



  /** Ahora apunta a la cuota mensual creada, no al tipo */
  @NotNull
  private Long cuotaMensualId;

  /** Permite activar/pausar desde edición (opcional) */
  private Boolean activo;

  // --------------- Sub-DTOs ---------------
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PersonaDTO {
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    @NotNull  private LocalDate fechaNacimiento;

    @NotBlank private String domicilio;

    @NotBlank private String telefonoArea;
    @NotBlank private String telefonoNumero;

    @Email private String email;

    private Long generoId;
    private Long localidadId;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContactoUrgenciaDTO {
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    @NotBlank private String telefonoArea;
    @NotBlank private String telefonoNumero;
    @NotBlank private String relacion;
    private Long localidadId;
  }
}
