package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.respuesta.DashboardResponse;
import com.romacontrol.romacontrol_v1.dto.respuesta.PagoDiaResponse;

public interface DashboardService {
    DashboardResponse obtenerDatosDashboard();
    

    List<PagoDiaResponse> obtenerPagosPorDia(int mes, int anio);

}
