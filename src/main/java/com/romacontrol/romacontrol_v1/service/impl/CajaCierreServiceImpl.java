package com.romacontrol.romacontrol_v1.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.model.Pago;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.model.cierre.CierreCompleto;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.repository.cierre.CierreCompletoRepository;
import com.romacontrol.romacontrol_v1.service.CajaCierreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajaCierreServiceImpl implements CajaCierreService {

    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CierreCompletoRepository cierreCompletoRepository;

    @Override
public void registrarCierreCompleto(LocalDate fecha) {

    // ============================================================
    // 1) Validar que NO exista un cierre completo previo del día
    // ============================================================

    var inicio = fecha.atStartOfDay().atOffset(java.time.ZoneOffset.UTC);      // FIX
    var fin    = fecha.plusDays(1).atStartOfDay().atOffset(java.time.ZoneOffset.UTC); // FIX

    boolean existe = !cierreCompletoRepository
            .findByFechaHoraCierreBetween(inicio, fin)
            .isEmpty();

    if (existe) {
        throw new IllegalStateException("Ya existe un cierre completo registrado para esta fecha.");
    }

    // ============================================================
    // 2) Obtener todos los pagos del día
    // ============================================================
    List<Pago> pagos = pagoRepository.findAll().stream()
            .filter(p -> p.getFechaPago().toLocalDate().isEqual(fecha))
            .toList();

    if (pagos.isEmpty()) {
        throw new IllegalStateException("No hay pagos registrados en esta fecha.");
    }

    // ============================================================
    // 3) Total general
    // ============================================================
    BigDecimal total = pagos.stream()
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // ============================================================
    // 4) Obtener ADMIN actual
    // ============================================================
    String dniActual = SecurityContextHolder.getContext().getAuthentication().getName();

    Usuario admin = usuarioRepository.findByDni(dniActual)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // ============================================================
    // 5) Registrar cierre completo
    // ============================================================
    CierreCompleto cierre = CierreCompleto.builder()
            .fechaHoraCierre(OffsetDateTime.now())   // fecha real del cierre
            .totalGeneral(total)
            .cerradoPor(admin)
            .observaciones(null)
            .build();

    cierreCompletoRepository.save(cierre);
}


}
