package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioDetailResponse {
  private Long id;
  private String dni;
  private boolean activo;
  private OffsetDateTime fechaCreacion;
  private Long tipoCuotaId;

  // Persona
  private String nombre;
  private String apellido;
  private LocalDate fechaNacimiento;
  private String domicilio;
  private String telefonoArea;
  private String telefonoNumero;
  private String email;
  private Long generoId;
  private String generoNombre;     // ✅ nombre legible (Femenino/Masculino/etc.)
  private Long localidadId;

  // Contacto de urgencia (si existe)
  private String contactoNombre;
  private String contactoApellido;
  private String contactoTelefonoArea;
  private String contactoTelefonoNumero;
  private String contactoRelacion;

  // Roles
  private List<Long> rolIds;

  // Creador (si existe)
  private Long creadoPorId;
  private String creadoPorNombre;  // "Apellido, Nombre"
  private String creadoPorDni;
}
