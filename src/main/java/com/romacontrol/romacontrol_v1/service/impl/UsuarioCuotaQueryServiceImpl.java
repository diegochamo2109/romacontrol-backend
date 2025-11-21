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
            ? List.of(UsuarioCuotaEstado.PENDIENTE, UsuarioCuotaEstado.PAGADA)
            : estados.stream()
                     .map(String::toUpperCase)
                     .map(UsuarioCuotaEstado::valueOf)
                     .toList();

    return usuarioCuotaRepo.findByUsuario_DniAndEstadoIn(dni, estadosEnum)
            .stream()
            .map(uc -> UsuarioCuotaItemResponse.builder()
                    .usuarioId(uc.getUsuario().getId())
                    .dni(uc.getUsuario().getDni())
                    .nombreCompleto(uc.getUsuario().getPersona().getNombre()
                                  + " " + uc.getUsuario().getPersona().getApellido())
                    .cuotaId(uc.getCuota().getId())
                    .descripcionCuota(uc.getCuota().getDescripcion())
                    .importe(uc.getCuota().getImporte())
                    .estado(uc.getEstado().name())
                    .conRetraso(uc.isConRetraso())
                    .fechaAsignacion(uc.getFechaAsignacion())
                    .fechaCambioEstado(uc.getFechaCambioEstado())
                    .fechaLimite(uc.getCuota().getFechaLimite())
                    .build())
            .toList();
}

}
