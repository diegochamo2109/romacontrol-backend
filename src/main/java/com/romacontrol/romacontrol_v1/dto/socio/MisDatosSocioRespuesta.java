package com.romacontrol.romacontrol_v1.dto.socio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MisDatosSocioRespuesta {

    // ============================
    // DATOS PERSONALES
    // ============================
    private String nombre;
    private String apellido;
    private String dni;

    private String calle;
    private String numero;

    private String fechaNacimiento;

    private String provincia;
    private String localidad;

    private String genero;

    private String telefonoArea;
    private String telefonoNumero;
    private String telefonoCompleto;

    private String email;

    // ============================
    // CONTACTO DE URGENCIA
    // ============================
    private String contactoNombre;
    private String contactoApellido;
    private String contactoRelacion;

    private String contactoProvincia;
    private String contactoLocalidad;

    private String contactoTelefonoArea;
    private String contactoTelefonoNumero;
}
