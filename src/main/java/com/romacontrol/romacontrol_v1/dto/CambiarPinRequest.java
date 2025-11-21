package com.romacontrol.romacontrol_v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CambiarPinRequest {
    @NotBlank private String pinActual;
    @NotBlank private String nuevoPin;
    @NotBlank private String confirmarPin;
}
