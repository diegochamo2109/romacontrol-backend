package com.romacontrol.romacontrol_v1.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.dto.CierreCompletoResponse;
import com.romacontrol.romacontrol_v1.dto.CierreParcialResponse;
import com.romacontrol.romacontrol_v1.model.Pago;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.service.CajaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final PagoRepository pagoRepository;

    @Override
    public List<CierreParcialResponse> obtenerCierreParcial(LocalDate fecha) {
        String usuarioActual = SecurityContextHolder.getContext().getAuthentication().getName();

        // 🔹 Filtra pagos cobrados por el usuario logueado en la fecha indicada
        List<Pago> pagos = pagoRepository.findAll().stream()
                .filter(p -> p.getCobradoPor() != null
                        && p.getCobradoPor().getDni().equals(usuarioActual)   // ← CORRECCIÓN AQUÍ
                        && p.getFechaPago().toLocalDate().isEqual(fecha))
                .collect(Collectors.toList());

        // 🔹 Convierte los pagos a DTOs de respuesta
        return pagos.stream()
                .map(CierreParcialResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CierreCompletoResponse> obtenerCierreCompleto(LocalDate fecha) {
        List<Pago> pagos = pagoRepository.findAll().stream()
                .filter(p -> p.getFechaPago().toLocalDate().isEqual(fecha))
                .collect(Collectors.toList());

        // Agrupa por nombre del cobrador
        Map<String, List<Pago>> pagosAgrupados = pagos.stream()
                .collect(Collectors.groupingBy(
                        (Pago p) -> String.valueOf(
                                p.getCobradoPor() != null && p.getCobradoPor().getPersona() != null
                                        ? p.getCobradoPor().getPersona().getNombre() + " " + p.getCobradoPor().getPersona().getApellido()
                                        : "Desconocido"
                        ),
                        Collectors.toList()
                ));

        return pagosAgrupados.entrySet().stream()
                .map(entry -> CierreCompletoResponse.fromGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // =============================================================
    // ⭐ NUEVO — Registrar cierre completo real (solo ADMIN)
    // =============================================================
    @Override
    public void cerrarCajaCompleta(LocalDate fecha) {
        // ⭐ Aquí vamos a implementar:
        //   1) Validar que no haya un cierre completo previo del día
        //   2) Leer cierres parciales existentes
        //   3) Calcular total general
        //   4) Guardar en la entidad nueva CierreCompleto
        //   5) Registrar fecha/hora y el usuario ADMIN que cerró
        //
        //   ❗ Ahora lo dejamos vacío hasta que definamos la entidad y el repositorio.
        
        throw new UnsupportedOperationException("cerrarCajaCompleta aún no implementado.");
    }
    // =============================================================

}
