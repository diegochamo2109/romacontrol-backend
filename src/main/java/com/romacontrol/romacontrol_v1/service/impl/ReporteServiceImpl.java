package com.romacontrol.romacontrol_v1.service.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.dto.HistorialPagoResponse;
import com.romacontrol.romacontrol_v1.dto.ResumenCuotaResponse;
import com.romacontrol.romacontrol_v1.repository.ReporteRepository;
import com.romacontrol.romacontrol_v1.service.ReporteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final ZoneId ZONA_ARG = ZoneId.of("America/Argentina/Buenos_Aires");

    @Override
    public List<HistorialPagoResponse> obtenerHistorialPagos(String dni, String desde, String hasta, String estado) {
        return reporteRepository.obtenerHistorialPagos(dni, desde, hasta, estado)
            .stream()
            .map(obj -> {
                OffsetDateTime fechaPago = parsearFecha(obj[0]);
                OffsetDateTime fechaVenc = parsearFecha(obj[4]);

                return new HistorialPagoResponse(
                    fechaPago != null ? fechaPago.atZoneSameInstant(ZONA_ARG).toLocalDateTime() : null,
                    (String) obj[1],     // dni
                    (String) obj[2],     // nombreCompleto
                    (String) obj[3],     // cuotaDescripcion
                    fechaVenc != null ? fechaVenc.atZoneSameInstant(ZONA_ARG).toLocalDateTime() : null,
                    (BigDecimal) obj[5], // monto
                    (String) obj[6],     // metodoPago
                    (String) obj[7],     // estado
                    (obj[8] != null) ? (Boolean) obj[8] : false,
                    (String) obj[9],     // cobradoPor
                    (String) obj[10]     // observacion
                );
            })
            .collect(Collectors.toList());
    }

    private OffsetDateTime parsearFecha(Object valor) {
        if (valor == null) return null;
        try {
            return OffsetDateTime.parse(valor.toString(), FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ResumenCuotaResponse> obtenerResumenCuotas() {
        return reporteRepository.obtenerResumenCuotas()
            .stream()
            .map(obj -> new ResumenCuotaResponse(
                (String) obj[0],
                (BigDecimal) obj[1],
                ((Number) obj[2]).longValue(),
                ((Number) obj[3]).longValue(),
                ((Number) obj[4]).longValue(),
                (BigDecimal) obj[5]
            ))
            .collect(Collectors.toList());
    }

    @Override
public List<HistorialPagoResponse> obtenerHistorialPagosCompleto(
        String dni,
        String desde,
        String hasta,
        String estado) {

    // 1️⃣ Obtenemos los pagos normales
    List<HistorialPagoResponse> pagos = obtenerHistorialPagos(dni, desde, hasta, estado);

    // 2️⃣ Si el filtro es "PENDIENTE", agregamos cuotas sin pagar
    if (estado != null && estado.equalsIgnoreCase("PENDIENTE")) {

        List<Object[]> pendientes = reporteRepository
                .obtenerCuotasPendientesOVencidas(dni, "PENDIENTE");

        List<HistorialPagoResponse> pendientesMapeados = pendientes.stream()
                .map(obj -> {

                    OffsetDateTime fechaVenc = parsearFecha(obj[0]);

                    return new HistorialPagoResponse(
                        null,                       // fechaPago (no existe porque NO pagó)
                        (String) obj[1],            // dni
                        (String) obj[2],            // nombreCompleto
                        (String) obj[3],            // cuotaDescripcion
                        fechaVenc != null
                               ? fechaVenc.atZoneSameInstant(ZONA_ARG).toLocalDateTime()
                               : null,
                        (BigDecimal) obj[5],        // monto
                        "—",                        // metodoPago
                        "PENDIENTE",                // estado
                        false,                      // conRetraso
                        "—",                        // cobradoPor
                        null                        // observacion
                    );
                })
                .collect(Collectors.toList());

        pagos.addAll(pendientesMapeados);
    }

    // 3️⃣ Ordenar: pagos con fecha primero, pendientes abajo
    pagos.sort((a, b) -> {
        if (a.getFechaPago() == null && b.getFechaPago() == null) return 0;
        if (a.getFechaPago() == null) return 1;
        if (b.getFechaPago() == null) return -1;
        return b.getFechaPago().compareTo(a.getFechaPago());
    });

    return pagos;
}

}
