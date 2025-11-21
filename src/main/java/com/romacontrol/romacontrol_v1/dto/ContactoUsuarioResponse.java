package com.romacontrol.romacontrol_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContactoUsuarioResponse {
  private Long idUsuario;
  private String dni;
  private String nombreCompleto;
  private boolean activo;

  private String nombreContacto;
  private String apellidoContacto;
  private String relacion;
  private String telefonoArea;
  private String telefonoNumero;
}
