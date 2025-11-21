package com.romacontrol.romacontrol_v1.service.cierre;

import java.time.LocalDate;
import java.util.List;

import com.romacontrol.romacontrol_v1.dto.cierre.CierreCompletoHistorialResponse;

public interface CierreCompletoHistorialService {

    /**
     * Obtiene todos los cierres completos realizados entre 'desde' y 'hasta'.
     */
    List<CierreCompletoHistorialResponse> historial(LocalDate desde, LocalDate hasta);
}
