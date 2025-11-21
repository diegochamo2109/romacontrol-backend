package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.CreacionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.dto.CuotaListadoDto;
import com.romacontrol.romacontrol_v1.dto.EdicionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;

public interface CuotaMensualService {

    CuotaMensual crearCuota(CreacionCuotaSolicitud dto, String dniCreador);

    List<CuotaListadoDto> listar();   // ← ESTA ES LA CLAVE

    CuotaMensual detalle(Long id);

    CuotaMensual editar(Long id, EdicionCuotaSolicitud dto, String dniEditor);

    void eliminar(Long id);

    CuotaMensual cambiarEstado(Long id, String nuevoEstado, String dniAdmin);

    CuotaMensual obtenerCuotaDelMes();
}
