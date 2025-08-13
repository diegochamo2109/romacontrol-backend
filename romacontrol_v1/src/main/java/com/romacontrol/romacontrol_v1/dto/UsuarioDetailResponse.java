package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Detalle completo para pantalla de edición */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDetailResponse {
  private Long id;
  private String dni;
  private boolean activo;
  private OffsetDateTime fechaCreacion;

  private Long creadoPorId;
  private String creadoPorDni;

  private PersonaDTO persona;
  private ContactoUrgenciaDTO contacto;

  private List<String> roles;   // nombres (ej. "ROLE_ADMIN")
  private List<Long> rolIds;    // ids seleccionados

  private Long tipoCuotaId;
  private String tipoCuotaNombre;

  // ----------------- Sub-DTOs -----------------
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PersonaDTO {
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String domicilio;
    private String telefonoArea;
    private String telefonoNumero;
    private String email;
    private Long generoId;
    private String generoNombre;
    private Long localidadId;
    private String localidadNombre;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContactoUrgenciaDTO {
    private String nombre;
    private String apellido;
    private String telefonoArea;
    private String telefonoNumero;
    private String relacion;
    private Long localidadId;
    private String localidadNombre;
  }
}
