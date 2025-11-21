package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.CuotaListadoResponse;

public interface CuotaDisponibleService {
    List<CuotaListadoResponse> listarCuotasNoVencidas();
}
