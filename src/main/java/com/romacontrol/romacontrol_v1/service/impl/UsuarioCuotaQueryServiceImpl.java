package com.romacontrol.romacontrol_v1.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.dto.UsuarioCuotaItemResponse;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.service.UsuarioCuotaQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioCuotaQueryServiceImpl implements UsuarioCuotaQueryService {

  private final UsuarioCuotaRepository usuarioCuotaRepo;

  @Override
  public List<UsuarioCuotaItemResponse> listarPorDniYEstados(String dni, List<String> estados) {
    var estadosEnum = (estados == null || estados.isEmpty())
        ? List.of(UsuarioCuotaEstado.PENDIENTE, UsuarioCuotaEstado.VENCIDA, UsuarioCuotaEstado.PAGADA)
        : estados.stream()
                 .map(String::toUpperCase)
                 .map(UsuarioCuotaEstado::valueOf)
                 .toList();

    return usuarioCuotaRepo.findByUsuario_DniAndEstadoIn(dni, estadosEnum)
        .stream()
        .map(uc -> UsuarioCuotaItemResponse.builder()
            .cuotaId(uc.getCuota().getId())
            .descripcion(uc.getCuota().getDescripcion())
            .estado(uc.getEstado().name())
            .conRetraso(uc.isConRetraso())
            .fechaLimiteIso(uc.getCuota().getFechaLimite().toString())
            .importe(uc.getCuota().getImporte())
            .build())
        .toList();
  }
}
