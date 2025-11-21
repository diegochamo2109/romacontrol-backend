package com.romacontrol.romacontrol_v1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.SocioAsistenciaResponse;
import com.romacontrol.romacontrol_v1.exception.NotFoundException;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.AsistenciaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * 🔹 Servicio que obtiene las asistencias del socio autenticado.
 *     - Solo lectura, no modifica nada.
 *     - Incluye hora de entrada, salida y si fue generada automáticamente.
 */
@Service
@RequiredArgsConstructor
public class SocioAsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<SocioAsistenciaResponse> listarMisAsistencias(String dni) {
        Usuario socio = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Socio no encontrado con DNI: " + dni));

        return asistenciaRepository.findByUsuarioId(socio.getId())
                .stream()
                .map(a -> SocioAsistenciaResponse.builder()
                        .id(a.getId())
                        .fechaRegistro(a.getFechaRegistro())
                        .horaEntrada(a.getHoraRegistro())
                        .horaSalida(a.getHoraSalida())
                        .salidaGeneradaAutomatica(a.isSalidaGeneradaAutomatica())
                        .observacion(a.getObservacion())
                        .build())
                .collect(Collectors.toList());
    }
}
