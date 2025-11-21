package com.romacontrol.romacontrol_v1.scheduler;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.EstadoCuota;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoCuotaRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CuotaScheduler {

    private final CuotaMensualRepository cuotaRepository;
    private final EstadoCuotaRepository estadoCuotaRepository;

    // ===========================================================
    // 1) Corrección automática al iniciar el servidor
    // ===========================================================
    @PostConstruct
    public void corregirEstadosAlArrancar() {
        log.info("🔄 Verificando estados de cuotas al iniciar el servidor...");

        procesarTransiciones();
    }

    // ===========================================================
    // 2) Scheduler diario a las 00:05 AM
    // ===========================================================
    @Scheduled(cron = "0 5 0 * * *")
    public void ejecutarSchedulerDiario() {
        log.info("⏱ Ejecutando scheduler diario de cuotas...");
        procesarTransiciones();
    }

    // ===========================================================
    // 3) Lógica compartida (profesional)
    // ===========================================================
    private void procesarTransiciones() {

        OffsetDateTime ahora = OffsetDateTime.now();

        EstadoCuota estadoProgramada =
                estadoCuotaRepository.findByNombreIgnoreCase("PROGRAMADA").orElse(null);

        EstadoCuota estadoDelMes =
                estadoCuotaRepository.findByNombreIgnoreCase("CUOTA_DEL_MES").orElse(null);

        EstadoCuota estadoVencida =
                estadoCuotaRepository.findByNombreIgnoreCase("VENCIDA").orElse(null);

        EstadoCuota estadoInactiva =
                estadoCuotaRepository.findByNombreIgnoreCase("INACTIVA").orElse(null);

        if (estadoProgramada == null || estadoDelMes == null || estadoVencida == null) {
            log.error("❌ Estados de cuota no configurados correctamente en la BD.");
            return;
        }

        // -----------------------------------------------------------
        // A) PROGRAMADA → CUOTA_DEL_MES
        // -----------------------------------------------------------
        List<CuotaMensual> programadas = cuotaRepository.findByEstadoCuota(estadoProgramada);

        for (CuotaMensual c : programadas) {
            if (!ahora.isBefore(c.getFechaAlta())) {
                c.setEstadoCuota(estadoDelMes);
                cuotaRepository.save(c);

                log.info("📆 Cuota {} pasó a CUOTA_DEL_MES", c.getDescripcion());
            }
        }

        // -----------------------------------------------------------
        // B) CUOTA_DEL_MES → VENCIDA
        // -----------------------------------------------------------
        List<CuotaMensual> vigentes = cuotaRepository.findByEstadoCuota(estadoDelMes);

        for (CuotaMensual c : vigentes) {
            if (ahora.isAfter(c.getFechaLimite())) {
                c.setEstadoCuota(estadoVencida);
                cuotaRepository.save(c);

                log.info("⛔ Cuota {} pasó a VENCIDA", c.getDescripcion());
            }
        }

        // -----------------------------------------------------------
        // C) VENCIDA → INACTIVA (después de 45 días)
        // -----------------------------------------------------------
        List<CuotaMensual> vencidas = cuotaRepository.findByEstadoCuota(estadoVencida);

        for (CuotaMensual c : vencidas) {
            if (c.getFechaLimite().plusDays(45).isBefore(ahora)) {
                c.setEstadoCuota(estadoInactiva);
                cuotaRepository.save(c);

                log.info("📁 Cuota {} pasó a INACTIVA", c.getDescripcion());
            }
        }
    }
}
