// src/main/java/com/romacontrol/romacontrol_v1/service/impl/AsistenciaSocioServiceImpl.java
package com.romacontrol.romacontrol_v1.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.dto.AsistenciaResponse;
import com.romacontrol.romacontrol_v1.model.Asistencia;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.AsistenciaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.AsistenciaSocioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsistenciaSocioServiceImpl implements AsistenciaSocioService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<AsistenciaResponse> listarPorUsuarioAutenticado(LocalDate desde, LocalDate hasta) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String dni = (auth != null) ? auth.getName() : null;

        if (dni == null || dni.isBlank()) {
            throw new RuntimeException("No se pudo identificar al usuario autenticado.");
        }

        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para el DNI autenticado."));

        List<Asistencia> asistencias;

        if (desde != null && hasta != null) {
            asistencias = asistenciaRepository.findByUsuarioIdAndFechaRegistroBetweenOrderByFechaRegistroDescHoraRegistroDesc(
                    usuario.getId(), desde, hasta);
        } else {
            asistencias = asistenciaRepository.findByUsuarioIdOrderByFechaRegistroDescHoraRegistroDesc(usuario.getId());
        }

        return asistencias.stream()
                .map(a -> {
                    LocalDateTime fechaHora = LocalDateTime.of(a.getFechaRegistro(), a.getHoraRegistro());
                    return new AsistenciaResponse(
                            a.getId(),
                            usuario.getPersona().getApellido() + ", " + usuario.getPersona().getNombre(),
                            usuario.getDni(),
                            fechaHora,
                            a.isIntentoExtra() ? "Duplicada" : "OK",
                            a.getObservacion()
                    );
                })
                .collect(Collectors.toList());
    }
}
