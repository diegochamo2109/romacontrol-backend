package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.UsuarioCuotaItemResponse;

public interface UsuarioCuotaQueryService {
  /** Lista cuotas por DNI filtrando por estados, ej: ["PENDIENTE","VENCIDA"]. */
  List<UsuarioCuotaItemResponse> listarPorDniYEstados(String dni, List<String> estados);
}
