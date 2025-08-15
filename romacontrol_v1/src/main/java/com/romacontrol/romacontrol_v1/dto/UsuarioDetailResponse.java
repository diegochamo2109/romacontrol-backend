

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
  private String cuotaNombre; // ✅ NUEVO campo para el nombre legible



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

  // Persona
    private Long provinciaId;     // 👈 necesario para que el frontend cargue localidades
    

    // Contacto de urgencia
    private Long contactoProvinciaId;     // 👈 nuevo
    private Long contactoLocalidadId;     // 👈 nuevo


  // Roles
  private List<Long> rolIds;
  private List<String> rolNombres;  // ✅ para mostrar roles como "ADMIN, PROFESOR"


  // Creador (si existe)
  private Long creadoPorId;
  private String creadoPorNombre;  // "Apellido, Nombre"
  private String creadoPorDni;
}
