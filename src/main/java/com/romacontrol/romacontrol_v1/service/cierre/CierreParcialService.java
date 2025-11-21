package com.romacontrol.romacontrol_v1.service.cierre;

import java.time.LocalDate;
import java.util.List;

import com.romacontrol.romacontrol_v1.dto.cierre.CierreParcialRequest;
import com.romacontrol.romacontrol_v1.dto.cierre.CierreParcialResponse;

public interface CierreParcialService {

    // Obtener cierre de un usuario en una fecha
    List<CierreParcialResponse> obtenerCierreDeUsuario(LocalDate fecha, Long usuarioId);

    // Obtener todos los cierres de una fecha
    List<CierreParcialResponse> obtenerCierresDeFecha(LocalDate fecha);

    // Crear un cierre parcial nuevo
    CierreParcialResponse generarCierre(CierreParcialRequest request);

    // Saber si ya se hizo cierre hoy
    boolean existeCierreHoy(Long usuarioId);
    
}
