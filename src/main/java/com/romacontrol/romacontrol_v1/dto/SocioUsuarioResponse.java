package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocioUsuarioResponse {

    private Long id;
    private String dni;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String domicilio;
    private String telefonoArea;
    private String telefonoNumero;
    private String email;
    private String genero;
    private String localidad;
    private String provincia;

    // Contacto
    private String contactoNombre;
    private String contactoApellido;
    private String contactoTelefonoArea;
    private String contactoTelefonoNumero;
    private String contactoRelacion;
    private String contactoLocalidad;
    private String contactoProvincia;

    // Foto en Base64
    private String fotoPerfilBase64;
}
