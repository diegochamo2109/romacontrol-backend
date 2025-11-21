

package com.romacontrol.romacontrol_v1.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.CreacionTipoGastoSolicitud;
import com.romacontrol.romacontrol_v1.dto.TipoGastoRespuesta;
import com.romacontrol.romacontrol_v1.model.TipoGasto;
import com.romacontrol.romacontrol_v1.repository.TipoGastoRepository;
import com.romacontrol.romacontrol_v1.service.TipoGastoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoGastoServiceImpl implements TipoGastoService {

    private final TipoGastoRepository tipoGastoRepository;

    @Override
    public TipoGastoRespuesta crearTipoGasto(CreacionTipoGastoSolicitud solicitud) {
        if (tipoGastoRepository.existsByNombreIgnoreCase(solicitud.getNombre())) {
            throw new RuntimeException("Ya existe un tipo de gasto con ese nombre.");
        }

        TipoGasto tipoGasto = TipoGasto.builder()
                .nombre(solicitud.getNombre())
                .descripcion(solicitud.getDescripcion())
                .build();

        tipoGastoRepository.save(tipoGasto);

        return TipoGastoRespuesta.builder()
                .id(tipoGasto.getId())
                .nombre(tipoGasto.getNombre())
                .descripcion(tipoGasto.getDescripcion())
                .fechaCreacion(tipoGasto.getFechaCreacion())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoGastoRespuesta> listarTodos() {
        return tipoGastoRepository.findAll().stream()
                .map(t -> TipoGastoRespuesta.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .descripcion(t.getDescripcion())
                        .fechaCreacion(t.getFechaCreacion())
                        .build())
                .collect(Collectors.toList());
    }
}
