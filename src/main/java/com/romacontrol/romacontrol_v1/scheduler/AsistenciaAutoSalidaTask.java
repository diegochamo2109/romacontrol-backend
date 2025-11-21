package com.romacontrol.romacontrol_v1.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.romacontrol.romacontrol_v1.model.Asistencia;
import com.romacontrol.romacontrol_v1.repository.AsistenciaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsistenciaAutoSalidaTask {

    private final AsistenciaRepository asistenciaRepository;

    /**
     * 🕒 Ejecuta cada 10 minutos (600 000 ms) y genera las salidas automáticas
     *     para asistencias que superaron las 3 horas desde la entrada.
     */
    @Scheduled(fixedRate = 600000)
    public void generarSalidasAutomaticas() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // Buscar asistencias de hoy sin salida
        List<Asistencia> sinSalida = asistenciaRepository.findByFechaRegistroAndHoraSalidaIsNull(hoy);

        for (Asistencia a : sinSalida) {
            try {
                if (a.getHoraRegistro() != null && a.getHoraRegistro().plusHours(2).isBefore(ahora)) {
                    a.setHoraSalida(a.getHoraRegistro().plusHours(2));
                    a.setSalidaGeneradaAutomatica(true);
                    a.setObservacion("Salida generada automáticamente por el sistema.");
                    asistenciaRepository.save(a);

                    log.info("💡 Salida automática generada para usuario {} a las {}",
                            a.getUsuario().getDni(), a.getHoraSalida());
                }
            } catch (Exception e) {
                log.error("⚠️ Error al generar salida automática para asistencia ID {}: {}", a.getId(), e.getMessage());
            }
        }
    }
}
