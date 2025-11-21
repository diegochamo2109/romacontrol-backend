package com.romacontrol.romacontrol_v1.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mostrar el historial de terminales.
 * Incluye información sobre quién la abrió y quién la cerró.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroTerminalResponse {

    private Long id;
    private String administrador;  // Usuario que abrió la terminal
    private String cerradoPor;     // 🆕 Usuario que la cerró
    private LocalDateTime fechaHoraApertura;
    private LocalDateTime fechaHoraCierre;
    private boolean terminalAbierta;
    private String observacion;
   
}
