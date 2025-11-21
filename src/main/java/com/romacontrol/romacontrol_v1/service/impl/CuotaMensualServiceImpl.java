package com.romacontrol.romacontrol_v1.service.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.CreacionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.dto.CuotaListadoDto;
import com.romacontrol.romacontrol_v1.dto.EdicionCuotaSolicitud;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.EstadoCuota;
import com.romacontrol.romacontrol_v1.model.TipoCuota;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.repository.TipoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.CuotaMensualService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CuotaMensualServiceImpl implements CuotaMensualService {

    private final CuotaMensualRepository cuotaRepository;
    private final EstadoCuotaRepository estadoCuotaRepository;
    private final TipoCuotaRepository tipoCuotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioCuotaRepository usuarioCuotaRepository;
    private final PagoRepository pagoRepository;

    // ===========================================================
    // 1) CREAR CUOTA
    // ===========================================================
    @Override
    public CuotaMensual crearCuota(CreacionCuotaSolicitud dto, String dniCreador) {

        if (cuotaRepository.existsByDescripcion(dto.getDescripcion())) {
            throw new IllegalArgumentException("Ya existe una cuota con esa descripción.");
        }

        OffsetDateTime inicio = dto.getFechaInicio()
                .atStartOfDay()
                .atOffset(ZoneOffset.of("-03:00"));

        OffsetDateTime limite = dto.getFechaVencimiento()
                .atTime(23, 59, 59)
                .atOffset(ZoneOffset.of("-03:00"));

        if (cuotaRepository.existeRangoSuperpuesto(inicio, limite)) {
            throw new IllegalArgumentException("Ya existe otra cuota en ese rango de fechas.");
        }

        Usuario creador = usuarioRepository.findByDni(dniCreador)
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado."));

        TipoCuota tipo = tipoCuotaRepository.findById(dto.getTipoCuotaId())
                .orElseThrow(() -> new RuntimeException("Tipo de cuota no encontrado."));

        EstadoCuota estadoProgramada = estadoCuotaRepository
                .findByNombre("PROGRAMADA")
                .orElseThrow(() -> new RuntimeException("Estado PROGRAMADA no existe."));

        CuotaMensual cuota = CuotaMensual.builder()
                .descripcion(dto.getDescripcion())
                .importe(dto.getImporte())
                .fechaAlta(inicio)
                .fechaLimite(limite)
                .tipoCuota(tipo)
                .estadoCuota(estadoProgramada)
                .creadoPor(creador)
                .activa(true)
                .build();

        cuotaRepository.save(cuota);

        if (dto.isAsignar()) {
            usuarioCuotaRepository.asignarMasivoATodosActivos(cuota.getId());
        }

        return cuota;
    }

    // ===========================================================
    // 2) LISTAR
    // ===========================================================
@Override
@Transactional(readOnly = true)
public List<CuotaListadoDto> listar() {

    return cuotaRepository.findAll()
            .stream()
            .map(c -> CuotaListadoDto.builder()
                    .id(c.getId())
                    .descripcion(c.getDescripcion())
                    .importe(c.getImporte())
                    .fechaAlta(c.getFechaAlta())
                    .fechaLimite(c.getFechaLimite())
                    .estado(c.getEstadoCuota().getNombre())
                    .tipo(c.getTipoCuota().getNombre())
                    .build()
            )
            .toList();
}

    // ===========================================================
    // 3) DETALLE
    // ===========================================================
    @Override
    @Transactional(readOnly = true)
    public CuotaMensual detalle(Long id) {
        return cuotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada."));
    }

    // ===========================================================
    // 4) EDITAR (solo PROGRAMADA)
    // ===========================================================
    @Override
    public CuotaMensual editar(Long id, EdicionCuotaSolicitud dto, String dniEditor) {

        CuotaMensual cuota = detalle(id);

        if (!cuota.getEstadoCuota().getNombre().equalsIgnoreCase("PROGRAMADA")) {
            throw new IllegalStateException("Solo se pueden editar cuotas en estado PROGRAMADA.");
        }

        OffsetDateTime inicio = dto.getFechaInicio()
                .atStartOfDay()
                .atOffset(ZoneOffset.of("-03:00"));

        OffsetDateTime limite = dto.getFechaVencimiento()
                .atTime(23, 59, 59)
                .atOffset(ZoneOffset.of("-03:00"));

        if (cuotaRepository.existeRangoSuperpuesto(inicio, limite)) {
            throw new IllegalArgumentException("Otra cuota se superpone con ese rango de fechas.");
        }

        Usuario editor = usuarioRepository.findByDni(dniEditor)
                .orElseThrow(() -> new RuntimeException("Usuario editor no encontrado."));

        cuota.setDescripcion(dto.getDescripcion());
        cuota.setImporte(dto.getImporte());
        cuota.setFechaAlta(inicio);
        cuota.setFechaLimite(limite);
        cuota.setModificadoPor(editor);
        cuota.setFechaModificacion(OffsetDateTime.now());

        return cuotaRepository.save(cuota);
    }

    // ===========================================================
    // 5) ELIMINAR (solo si no tiene pagos)
    // ===========================================================
    @Override
    public void eliminar(Long id) {

        CuotaMensual cuota = detalle(id);

        if (pagoRepository.existsByCuotaMensual_Id(id)) {
            throw new IllegalStateException("No se puede eliminar esta cuota porque tiene pagos registrados.");
        }

        cuotaRepository.delete(cuota);
    }

    // ===========================================================
    // 6) CAMBIAR ESTADO MANUAL
    // ===========================================================
    @Override
    public CuotaMensual cambiarEstado(Long id, String nuevoEstado, String dniAdmin) {

        CuotaMensual cuota = detalle(id);

        EstadoCuota estado = estadoCuotaRepository.findByNombreIgnoreCase(nuevoEstado)
                .orElseThrow(() -> new RuntimeException("Estado inválido."));

        if (estado.getNombre().equalsIgnoreCase("CUOTA_DEL_MES")) {
            throw new IllegalStateException("El estado CUOTA_DEL_MES solo se asigna automáticamente.");
        }

        if (estado.getNombre().equalsIgnoreCase("VENCIDA") &&
                OffsetDateTime.now().isBefore(cuota.getFechaLimite())) {
            throw new IllegalStateException("No puede marcarse VENCIDA antes de la fecha límite.");
        }

        Usuario admin = usuarioRepository.findByDni(dniAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado."));

        cuota.setEstadoCuota(estado);
        cuota.setModificadoPor(admin);
        cuota.setFechaModificacion(OffsetDateTime.now());

        return cuotaRepository.save(cuota);
    }

    // ===========================================================
    // 7) OBTENER CUOTA DEL MES
    // ===========================================================
    @Override
    @Transactional(readOnly = true)
    public CuotaMensual obtenerCuotaDelMes() {

        return cuotaRepository
                .findFirstByEstadoCuota_NombreIgnoreCaseOrderByFechaAltaDesc("CUOTA_DEL_MES")
                .orElseThrow(() ->
                        new RuntimeException("No hay cuota configurada como CUOTA_DEL_MES."));
    }
}
