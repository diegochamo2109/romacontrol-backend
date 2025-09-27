package com.romacontrol.romacontrol_v1.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.CuotaCreateRequest;
import com.romacontrol.romacontrol_v1.dto.CuotaDetailResponse;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.UsuarioCuota;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.TipoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuotaServiceImpl implements com.romacontrol.romacontrol_v1.service.CuotaService {

  private final CuotaMensualRepository cuotaRepo;
  private final UsuarioRepository usuarioRepo;
  private final UsuarioCuotaRepository usuarioCuotaRepo;
  private final EstadoCuotaRepository estadoCuotaRepo;
  private final TipoCuotaRepository tipoCuotaRepo;

  @Transactional
  @Override
public List<CuotaDetailResponse> listarTodas() {
    return cuotaRepo.findAll().stream()
        .map(c -> CuotaDetailResponse.builder()
            .id(c.getId())
            .descripcion(c.getDescripcion())
            .importe(c.getImporte())
            .fechaAlta(c.getFechaAlta())
            .fechaLimite(c.getFechaLimite())
            .estado(c.getEstadoCuota().getNombre())
            .tipo(c.getTipoCuota().getNombre())
            .activa(c.isActiva())
            .build()
        ).toList();
}

  @Override
  public CuotaDetailResponse crearYAsignar(CuotaCreateRequest req, String usernameActual, boolean asignar) {
    var creador = usuarioRepo.findByDni(usernameActual)
        .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));

    // 👇 Determinar estado según asignar
    var estado = estadoCuotaRepo.findByNombre(asignar ? "ACTIVA" : "INACTIVA")
        .orElseThrow(() -> new IllegalArgumentException("Estado de cuota no encontrado"));

    var tipo = tipoCuotaRepo.findById(req.getTipoCuotaId())
        .orElseThrow(() -> new IllegalArgumentException("TipoCuota no existe"));

    var ahora = OffsetDateTime.now();

    var cuota = CuotaMensual.builder()
        .descripcion(req.getDescripcion())
        .importe(req.getImporte())
        .fechaAlta(ahora)
        .fechaLimite(req.getFechaVencimiento().atStartOfDay().atOffset(OffsetDateTime.now().getOffset()))

        .estadoCuota(estado)
        .tipoCuota(tipo)
        .activa(asignar)  // 🚩 activa solo si asignar = true
        .creadoPor(creador)
        .build();

    cuota = cuotaRepo.save(cuota);

    int totalAsignadas = 0;
    if (asignar) {
      totalAsignadas = asignarATodosLosActivos(cuota);
    }

    return CuotaDetailResponse.builder()
        .id(cuota.getId())
        .descripcion(cuota.getDescripcion())
        .importe(cuota.getImporte())
        .fechaAlta(cuota.getFechaAlta())
        .fechaLimite(cuota.getFechaLimite())
        .estado(cuota.getEstadoCuota().getNombre())
        .tipo(cuota.getTipoCuota().getNombre())
        .activa(cuota.isActiva())
        .totalAsignadas(totalAsignadas)
        .build();
  }

  /** Asigna PENDIENTE a todos los usuarios activos que no tengan esta cuota. */
  private int asignarATodosLosActivos(CuotaMensual cuota) {
    var ahora = OffsetDateTime.now();
    var usuariosActivos = usuarioRepo.findByActivoTrue();
    int count = 0;

    for (var u : usuariosActivos) {
      if (!usuarioCuotaRepo.existsByUsuarioIdAndCuotaId(u.getId(), cuota.getId())) {
        var uc = UsuarioCuota.builder()
            .usuario(u)
            .cuota(cuota)
            .estado(UsuarioCuotaEstado.PENDIENTE)
            .fechaAsignacion(ahora)
            .build();
        usuarioCuotaRepo.save(uc);
        count++;
      }
    }
    return count;
  }
}
