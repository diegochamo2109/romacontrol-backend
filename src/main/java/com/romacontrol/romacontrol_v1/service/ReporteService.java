package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.HistorialPagoResponse;
import com.romacontrol.romacontrol_v1.dto.ResumenCuotaResponse;

public interface ReporteService {
    List<HistorialPagoResponse> obtenerHistorialPagos(String dni, String desde, String hasta, String estado);
    List<ResumenCuotaResponse> obtenerResumenCuotas();
        // 🆕 NUEVO (por ahora solo declaramos, no cambia nada)
    List<HistorialPagoResponse> obtenerHistorialPagosCompleto(
            String dni,
            String desde,
            String hasta,
            String estado
    );
}
