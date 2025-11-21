package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.CuotaCreateRequest;
import com.romacontrol.romacontrol_v1.dto.CuotaDetailResponse;
import com.romacontrol.romacontrol_v1.dto.CuotaEditRequest;
import com.romacontrol.romacontrol_v1.dto.CuotaListadoResponse;

public interface CuotaService {

  CuotaDetailResponse crearYAsignar(CuotaCreateRequest req, String usernameActual, boolean asignar);

  List<CuotaDetailResponse> listarTodas();

  List<CuotaListadoResponse> listarConContadores();

  /** ✏️ Editar una cuota existente */
  CuotaDetailResponse editarCuota(CuotaEditRequest request, String usuarioModificador);

  /** ❌ Eliminación lógica (solo si no tiene pagos asociados) */
  void eliminarLogicamente(Long id, String usuarioModificador);

  List<CuotaDetailResponse> listarActivas();
  
    // 🔹 NUEVO → necesario para la cuota del mes
    CuotaDetailResponse obtenerCuotaDelMes();

    

}
