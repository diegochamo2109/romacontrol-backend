package com.romacontrol.romacontrol_v1.dto.socio;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para que el SOCIO edite únicamente su propia información.
 * Similar a UsuarioUpdateRequest pero sin roles, cuota y estado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MisDatosSocioUpdateRequest {

    /** Si viene con 4 dígitos se actualiza el PIN, si viene null o vacío NO se modifica */
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe tener 4 dígitos")
    private String pin;

    @NotNull(message = "Los datos personales son obligatorios")
    private PersonaDTO persona;

    @NotNull(message = "El contacto de urgencia es obligatorio")
    private ContactoUrgenciaDTO contacto;

    // ============================
    // SUB-DTOs
    // ============================

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

        @NotBlank(message = "La calle es obligatoria")
        private String calle;

        @NotBlank(message = "El número del domicilio es obligatorio")
        private String numero;

        @Email(message = "Formato de e-mail inválido")
        private String email;

        // Teléfono
        @NotBlank(message = "El código de área es obligatorio")
        private String telefonoArea;

        @NotBlank(message = "El número de teléfono es obligatorio")
        private String telefonoNumero;

        // Catálogos
        @NotNull(message = "Debe elegir una provincia")
        private Long provinciaId;

        @NotNull(message = "Debe elegir una localidad")
        private Long localidadId;

        @NotNull(message = "Debe elegir un género")
        private Long generoId;
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

        @NotBlank(message = "Debe indicar la relación")
        private String relacion;

        @NotBlank(message = "El código de área del contacto es obligatorio")
        private String telefonoArea;

        @NotBlank(message = "El número del contacto es obligatorio")
        private String telefonoNumero;

        @NotNull(message = "La provincia es obligatoria")
        private Long provinciaId;

        @NotNull(message = "La localidad es obligatoria")
        private Long localidadId;
    }
}
