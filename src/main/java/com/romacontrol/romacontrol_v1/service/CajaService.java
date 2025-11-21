package com.romacontrol.romacontrol_v1.service;

import java.time.LocalDate;
import java.util.List;

import com.romacontrol.romacontrol_v1.dto.CierreCompletoResponse;
import com.romacontrol.romacontrol_v1.dto.CierreParcialResponse;

public interface CajaService {

    // ---------------------------
    // CIERRE PARCIAL (lo que ya funciona)
    // ---------------------------
    List<CierreParcialResponse> obtenerCierreParcial(LocalDate fecha);

    // ---------------------------
    // CIERRE COMPLETO (lectura actual)
    // ---------------------------
    List<CierreCompletoResponse> obtenerCierreCompleto(LocalDate fecha);

    // ---------------------------
    // NUEVO: Registrar el cierre completo del día
    // ---------------------------
    void cerrarCajaCompleta(LocalDate fecha);

}
