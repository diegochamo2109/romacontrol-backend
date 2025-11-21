package com.romacontrol.romacontrol_v1.service;

import com.romacontrol.romacontrol_v1.dto.CuotaDetailResponse;
import com.romacontrol.romacontrol_v1.dto.CuotaEditRequest;

public interface CuotaEstadoService {
    CuotaDetailResponse cambiarEstado(Long id, CuotaEditRequest request);
}
