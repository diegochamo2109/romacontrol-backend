package com.romacontrol.romacontrol_v1.service;

import com.romacontrol.romacontrol_v1.dto.CreacionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;

public interface CuotaMensualService {
    CuotaMensual crearCuota(CreacionCuotaSolicitud dto, String dniCreador);
}
