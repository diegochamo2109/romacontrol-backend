package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.CuotaCreateRequest;
import com.romacontrol.romacontrol_v1.dto.CuotaDetailResponse;

public interface CuotaService {

  /**
   * Crea una cuota y, si el parámetro "asignar" es true,
   * la asigna automáticamente a todos los usuarios activos.
   */
  CuotaDetailResponse crearYAsignar(CuotaCreateRequest req, String usernameActual, boolean asignar);
  List<CuotaDetailResponse> listarTodas();
}
