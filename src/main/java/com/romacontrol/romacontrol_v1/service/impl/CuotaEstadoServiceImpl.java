package com.romacontrol.romacontrol_v1.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.CuotaDetailResponse;
import com.romacontrol.romacontrol_v1.dto.CuotaEditRequest;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.EstadoCuota;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoCuotaRepository;
import com.romacontrol.romacontrol_v1.service.CuotaEstadoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CuotaEstadoServiceImpl implements CuotaEstadoService {

    private final CuotaMensualRepository cuotaRepo;
    private final EstadoCuotaRepository estadoCuotaRepo;

    @Override
    public CuotaDetailResponse cambiarEstado(Long id, CuotaEditRequest request) {
        log.info("🔁 Cambiando estado de cuota ID={} a {}", id, request.isActiva() ? "ACTIVA" : "INACTIVA");

        CuotaMensual cuota = cuotaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Cuota no encontrada con ID " + id));

        // 🔹 Buscar el estado correspondiente (ACTIVA o INACTIVA)
        String nombreEstado = request.isActiva() ? "ACTIVA" : "INACTIVA";
        EstadoCuota nuevoEstado = estadoCuotaRepo.findByNombreIgnoreCase(nombreEstado)
                .orElseThrow(() -> new RuntimeException("❌ Estado no encontrado: " + nombreEstado));

        // 🔹 Actualizar campos coherentes
        cuota.setActiva(request.isActiva());
        cuota.setEstadoCuota(nuevoEstado);

        // 🔹 Guardar en BD
        cuotaRepo.save(cuota);

        log.info("✅ Estado de cuota {} cambiado correctamente a {}", cuota.getId(), nombreEstado);

        // 🔹 Retornar DTO actualizado
        return CuotaDetailResponse.builder()
                .id(cuota.getId())
                .descripcion(cuota.getDescripcion())
                .importe(cuota.getImporte())
                .fechaAlta(cuota.getFechaAlta())
                .fechaLimite(cuota.getFechaLimite())
                .estado(nuevoEstado.getNombre())
                .tipo(cuota.getTipoCuota() != null ? cuota.getTipoCuota().getNombre() : "-")
                .activa(cuota.isActiva())
                .build();
    }
}
