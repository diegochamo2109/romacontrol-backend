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

  @NotNull(message = "Los datos personales son obligatorios")
  private PersonaDTO persona;

  @NotNull(message = "Los datos de contacto de urgencia son obligatorios")
  private ContactoUrgenciaDTO contacto;

  @NotNull(message = "Debe tener al menos un rol asignado")
  private List<Long> rolIds;

  /** 
   * Cuota mensual asociada.
   * 🔹 NUEVO: ya no es @NotNull, porque ADMIN/PROFESOR pueden no tener cuota asignada.
   * El Service valida este campo solo cuando el rol incluye 'SOCIO'.
   */
  private Long cuotaMensualId; // 🔹 NUEVO (antes tenía @NotNull)

  /** Permite activar/pausar desde edición (opcional) */
  private Boolean activo;

  // --------------- Sub-DTOs ---------------

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PersonaDTO {
    @NotBlank(message = "El nombre es obligatorio") 
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio") 
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")  
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El domicilio es obligatorio") 
    private String domicilio;

    @NotBlank(message = "El código de área es obligatorio") 
    private String telefonoArea;

    @NotBlank(message = "El número de teléfono es obligatorio") 
    private String telefonoNumero;

    @Email(message = "Formato de correo electrónico inválido") 
    private String email;

    private Long generoId;
    private Long localidadId;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContactoUrgenciaDTO {
    @NotBlank(message = "El nombre del contacto es obligatorio") 
    private String nombre;

    @NotBlank(message = "El apellido del contacto es obligatorio") 
    private String apellido;

    @NotBlank(message = "El código de área del contacto es obligatorio") 
    private String telefonoArea;

    @NotBlank(message = "El número del contacto es obligatorio") 
    private String telefonoNumero;

    @NotBlank(message = "Debe indicar la relación con el contacto") 
    private String relacion;

    private Long localidadId;
  }
}
