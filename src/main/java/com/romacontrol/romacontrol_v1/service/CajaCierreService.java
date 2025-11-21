package com.romacontrol.romacontrol_v1.service;

import java.time.LocalDate;

public interface CajaCierreService {

    /**
     * Registra el cierre completo del día.
     * Solo ADMIN puede ejecutarlo.
     */
    void registrarCierreCompleto(LocalDate fecha);
    
}
