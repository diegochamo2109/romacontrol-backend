package com.romacontrol.romacontrol_v1.service.socio;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.socio.AsistenciaSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.CuotaSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.MisDatosSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.PagoSocioRespuesta;

public interface SocioService {

    MisDatosSocioRespuesta obtenerMisDatos();

    List<CuotaSocioRespuesta> obtenerMisCuotasPendientesYVencidas();

    List<PagoSocioRespuesta> obtenerMisPagos();

    List<AsistenciaSocioRespuesta> obtenerMisAsistencias();

   
}
