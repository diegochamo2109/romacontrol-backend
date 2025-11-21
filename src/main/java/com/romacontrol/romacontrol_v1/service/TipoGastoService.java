package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.CreacionTipoGastoSolicitud;
import com.romacontrol.romacontrol_v1.dto.TipoGastoRespuesta;

public interface TipoGastoService {
    TipoGastoRespuesta crearTipoGasto(CreacionTipoGastoSolicitud solicitud);
    List<TipoGastoRespuesta> listarTodos();
}
