
package com.romacontrol.romacontrol_v1.service;

import java.time.LocalDate;
import java.util.List;

import com.romacontrol.romacontrol_v1.dto.AsistenciaResponse;

public interface AsistenciaSocioService {
    List<AsistenciaResponse> listarPorUsuarioAutenticado(LocalDate desde, LocalDate hasta);
}
