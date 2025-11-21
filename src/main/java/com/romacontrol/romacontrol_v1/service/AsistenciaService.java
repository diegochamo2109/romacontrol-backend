package com.romacontrol.romacontrol_v1.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.AsistenciaResponse;
import com.romacontrol.romacontrol_v1.model.Asistencia;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.AsistenciaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 🔹 Registrar asistencia de usuario (terminal).
     * 
     * - Si ya registró entrada hoy → marca intento duplicado.
     * - Si no registró → crea una nueva asistencia.
     * - Además, antes de registrar, verifica si hay asistencias sin salida y
     *   genera automáticamente la salida si ya pasaron 3 horas.
     */
    @Transactional
    public Map<String, Object> registrarAsistencia(String dni, String pin) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            Usuario usuario = usuarioRepository.findByDni(dni)
                    .orElseThrow(() -> new RuntimeException("No se encontró un usuario con ese DNI."));

            if (!passwordEncoder.matches(pin, usuario.getPin())) {
                respuesta.put("mensaje", "El PIN ingresado no es correcto. Verifíquelo e intente nuevamente.");
                return respuesta;
            }

            // 🔹 Antes de registrar, actualizamos las salidas automáticas pendientes
            generarSalidasAutomaticas(usuario);

            LocalDate hoy = LocalDate.now();
            boolean yaRegistrado = asistenciaRepository.existsByUsuarioAndFechaRegistro(usuario, hoy);

            Asistencia asistencia = new Asistencia();
            asistencia.setUsuario(usuario);
            asistencia.setFechaRegistro(hoy);
            asistencia.setHoraRegistro(LocalTime.now());
            asistencia.setIntentoExtra(yaRegistrado);

            if (yaRegistrado) {
                asistencia.setObservacion("Intento duplicado de asistencia");
                asistenciaRepository.save(asistencia);
                respuesta.put("mensaje", "No es posible registrar la asistencia nuevamente hoy.");
            } else {
                asistencia.setObservacion("Asistencia registrada correctamente");
                asistenciaRepository.save(asistencia);
                respuesta.put("mensaje", "Asistencia registrada con éxito");
                respuesta.put("nombre", usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
            }

        } catch (RuntimeException e) {
            respuesta.put("mensaje", e.getMessage());
        } catch (Exception e) {
            respuesta.put("mensaje", "Ocurrió un error inesperado al registrar la asistencia. Intente nuevamente más tarde.");
        }

        return respuesta;
    }

    /**
     * ⏰ Genera salidas automáticas para asistencias del usuario sin salida después de 3 horas.
     */
    private void generarSalidasAutomaticas(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // Solo verificamos las asistencias del mismo usuario sin hora de salida
        List<Asistencia> sinSalida = asistenciaRepository.findByUsuarioAndFechaRegistroAndHoraSalidaIsNull(usuario, hoy);

        for (Asistencia a : sinSalida) {
            LocalTime horaEntrada = a.getHoraRegistro();
            if (horaEntrada != null && horaEntrada.plusHours(3).isBefore(ahora)) {
                a.setHoraSalida(horaEntrada.plusHours(3));
                a.setSalidaGeneradaAutomatica(true);
                a.setObservacion("Salida generada automáticamente a las 3 horas.");
                asistenciaRepository.save(a);
            }
        }
    }

    /**
     * 📋 Listar todas las asistencias registradas (para el panel de gestión).
     */
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listarAsistencias() {
        return asistenciaRepository.findAll()
                .stream()
                .map(a -> new AsistenciaResponse(
                        a.getId(),
                        a.getUsuario().getPersona().getNombre() + " " + a.getUsuario().getPersona().getApellido(),
                        a.getUsuario().getDni(),
                        a.getFechaRegistro().atTime(a.getHoraRegistro()),
                        a.isIntentoExtra() ? "Duplicado" : "Registrada",
                        a.getObservacion()
                ))
                .collect(Collectors.toList());
    }
}
