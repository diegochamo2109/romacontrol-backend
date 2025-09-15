package com.romacontrol.romacontrol_v1.dto;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioListItem {
   private Long id;
  private String dni;
  private String nombreCompleto;  // "Apellido, Nombre" del usuario
  private List<String> roles;     // ej. ["ADMIN"]
  private Boolean activo;
  private OffsetDateTime fechaCreacion;

  private String creadoPorDni;      // DNI del creador (si existe)
  private String creadoPorNombre;   // "Apellido, Nombre" del creador (si existe)
}
