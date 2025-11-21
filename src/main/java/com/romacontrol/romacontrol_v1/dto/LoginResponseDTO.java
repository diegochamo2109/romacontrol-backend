package com.romacontrol.romacontrol_v1.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {

    private String dni;
    private String nombre;
    private String apellido;
    private List<String> rolesShort;

    // 🔹 NUEVO: lista de rutas permitidas
    private List<String> permisos;
}
