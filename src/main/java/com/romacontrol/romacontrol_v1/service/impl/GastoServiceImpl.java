package com.romacontrol.romacontrol_v1.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.CreacionGastoSolicitud;
import com.romacontrol.romacontrol_v1.dto.GastoEdicionSolicitud;
import com.romacontrol.romacontrol_v1.dto.GastoListadoRespuesta;
import com.romacontrol.romacontrol_v1.dto.GastoRespuesta;
import com.romacontrol.romacontrol_v1.model.Gasto;
import com.romacontrol.romacontrol_v1.model.TipoGasto;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.GastoRepository;
import com.romacontrol.romacontrol_v1.repository.TipoGastoRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.GastoService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de gastos del sistema RomaControl.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final TipoGastoRepository tipoGastoRepository;
    private final UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd-yyyy | HH:mm").withLocale(new Locale("es", "AR"));

    // =====================================================
    // 🔹 CREAR GASTO
    // =====================================================
    @Override
    public GastoRespuesta crearGasto(CreacionGastoSolicitud solicitud, String dniUsuarioLogueado) {
        TipoGasto tipoGasto = tipoGastoRepository.findById(solicitud.getTipoGastoId())
                .orElseThrow(() -> new RuntimeException("Tipo de gasto no encontrado."));

        Usuario usuario = usuarioRepository.findByDni(dniUsuarioLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        Gasto gasto = Gasto.builder()
                .descripcion(solicitud.getDescripcion())
                .monto(solicitud.getMonto())
                .tipoGasto(tipoGasto)
                .registradoPor(usuario)
                .activo(true) // 🔹 todos los nuevos gastos comienzan activos
                .build();

        gastoRepository.save(gasto);

        return GastoRespuesta.builder()
                .id(gasto.getId())
                .tipoGastoNombre(tipoGasto.getNombre())
                .monto(gasto.getMonto())
                .descripcion(
                        (gasto.getDescripcion() == null || gasto.getDescripcion().isBlank())
                                ? "Sin observaciones"
                                : gasto.getDescripcion())
                .registradoPorNombre(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido())
                .fechaFormateada(gasto.getFecha().format(FORMATTER))
                .build();
    }

    // =====================================================
    // 🔹 LISTAR GASTOS SIMPLES (para historial)
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public List<GastoRespuesta> listarTodos() {
        return gastoRepository.findAll().stream()
                .map(g -> GastoRespuesta.builder()
                        .id(g.getId())
                        .tipoGastoNombre(g.getTipoGasto().getNombre())
                        .monto(g.getMonto())
                        .descripcion(
                                (g.getDescripcion() == null || g.getDescripcion().isBlank())
                                        ? "Sin observaciones"
                                        : g.getDescripcion())
                        .registradoPorNombre(
                                g.getRegistradoPor().getPersona().getNombre() + " "
                                        + g.getRegistradoPor().getPersona().getApellido())
                        .fechaFormateada(g.getFecha().format(FORMATTER))
                        .build())
                .collect(Collectors.toList());
    }

    // =====================================================
    // 🔹 LISTAR PARA GESTIONAR GASTOS
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public List<GastoListadoRespuesta> listarParaGestion() {
        return gastoRepository.findAll().stream()
                .map(g -> GastoListadoRespuesta.builder()
                        .id(g.getId())
                        .tipoGastoNombre(g.getTipoGasto().getNombre())
                        .tipoGastoId(g.getTipoGasto().getId())
                        .monto(g.getMonto())
                        .descripcion(
                                (g.getDescripcion() == null || g.getDescripcion().isBlank())
                                        ? "Sin observaciones"
                                        : g.getDescripcion())
                        .registradoPorNombre(
                                g.getRegistradoPor().getPersona().getNombre() + " "
                                        + g.getRegistradoPor().getPersona().getApellido())
                        .fechaFormateada(g.getFecha().format(FORMATTER))
                        .activo(Boolean.TRUE.equals(g.getActivo()))
                        .build())
                .collect(Collectors.toList());
    }

    // =====================================================
    // 🔹 EDITAR GASTO
    // =====================================================
    @Override
    public GastoListadoRespuesta editar(Long id, GastoEdicionSolicitud solicitud, String dniUsuarioLogueado) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado."));

        TipoGasto tipoGasto = tipoGastoRepository.findById(solicitud.getTipoGastoId())
                .orElseThrow(() -> new RuntimeException("Tipo de gasto no encontrado."));

        gasto.setTipoGasto(tipoGasto);
        gasto.setMonto(solicitud.getMonto());
        gasto.setDescripcion(solicitud.getDescripcion());

        gastoRepository.save(gasto);

        return GastoListadoRespuesta.builder()
                .id(gasto.getId())
                .tipoGastoNombre(tipoGasto.getNombre())
                .tipoGastoId(tipoGasto.getId())
                .monto(gasto.getMonto())
                .descripcion(
                        (gasto.getDescripcion() == null || gasto.getDescripcion().isBlank())
                                ? "Sin observaciones"
                                : gasto.getDescripcion())
                .registradoPorNombre(
                        gasto.getRegistradoPor().getPersona().getNombre() + " "
                                + gasto.getRegistradoPor().getPersona().getApellido())
                .fechaFormateada(gasto.getFecha().format(FORMATTER))
                .activo(Boolean.TRUE.equals(gasto.getActivo()))
                .build();
    }

    // =====================================================
    // 🔹 CAMBIO DE ESTADO (ELIMINACIÓN LÓGICA)
    // =====================================================
    @Override
    public void cambiarActivo(Long id, boolean activo, String dniUsuarioLogueado) {
        Gasto gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado."));

        gasto.setActivo(activo);
        gastoRepository.save(gasto);
    }
}
