package com.romacontrol.romacontrol_v1.dto;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fila compacta para tabla/listado */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioListItem {
  private Long id;
  private String dni;
  private String nombreCompleto;   // "Apellido, Nombre"
  private List<String> roles;      // p.ej. ["ROLE_ADMIN", "ROLE_SOCIO"]
  private boolean activo;
  private OffsetDateTime fechaCreacion;
  private String creadoPor;        // p.ej. "90000000 - Admin"
}
