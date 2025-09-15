package com.romacontrol.romacontrol_v1.service.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.CreacionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.EstadoCuota;
import com.romacontrol.romacontrol_v1.model.TipoCuota;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.model.UsuarioCuota;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.TipoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.CuotaMensualService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuotaMensualServiceImpl implements CuotaMensualService {

  private final CuotaMensualRepository cuotaRepository;
  private final UsuarioRepository usuarioRepository;
  private final TipoCuotaRepository tipoCuotaRepository;
  private final EstadoCuotaRepository estadoCuotaRepository;
  private final UsuarioCuotaRepository usuarioCuotaRepository;

  @Override
  @Transactional
  public CuotaMensual crearCuota(CreacionCuotaSolicitud dto, String dniCreador) {

    if (cuotaRepository.existsByDescripcion(dto.getDescripcion())) {
      throw new IllegalArgumentException("Ya existe una cuota con esa descripción.");
    }

    Usuario creador = usuarioRepository.findByDni(dniCreador)
        .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));

    TipoCuota tipo = tipoCuotaRepository.findById(dto.getTipoCuotaId())
        .orElseThrow(() -> new RuntimeException("Tipo de cuota no encontrado"));

    EstadoCuota estado = estadoCuotaRepository.findByNombre("ACTIVA")
        .orElseThrow(() -> new RuntimeException("Estado de cuota 'ACTIVA' no encontrado"));

    CuotaMensual cuota = CuotaMensual.builder()
        .descripcion(dto.getDescripcion())
        .importe(dto.getImporte())
        .fechaAlta(dto.getFechaInicio().atStartOfDay().atOffset(ZoneOffset.of("-03:00")))
        .fechaLimite(dto.getFechaVencimiento().atStartOfDay().atOffset(ZoneOffset.of("-03:00")))
        .tipoCuota(tipo)
        .estadoCuota(estado)
        .activa(true)
        .creadoPor(creador)
        .build();

    cuotaRepository.save(cuota);

    if (dto.isAsignar()) {
      List<Usuario> usuariosActivos = usuarioRepository.findByActivoTrue();
      for (Usuario usuario : usuariosActivos) {
        UsuarioCuota uc = UsuarioCuota.builder()
            .usuario(usuario)
            .cuota(cuota)
            .estado(UsuarioCuotaEstado.PENDIENTE)
            .fechaAsignacion(OffsetDateTime.now())
            .conRetraso(false)
            .build();
        usuarioCuotaRepository.save(uc);
      }
    }

    return cuota;
  }
}
