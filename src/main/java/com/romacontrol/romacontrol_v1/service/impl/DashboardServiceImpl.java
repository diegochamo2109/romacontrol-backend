package com.romacontrol.romacontrol_v1.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.dto.respuesta.DashboardResponse;
import com.romacontrol.romacontrol_v1.dto.respuesta.PagoDiaResponse;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.repository.rol.RolRepository;
import com.romacontrol.romacontrol_v1.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final CuotaMensualRepository cuotaMensualRepository;
    private final PagoRepository pagoRepository;
    private final RolRepository rolRepository;

    @Override
    public DashboardResponse obtenerDatosDashboard() {

        long totalUsuarios = usuarioRepository.count();
        long totalCuotas = cuotaMensualRepository.count();
        long totalPagos = pagoRepository.count();
        long totalRoles = rolRepository.count();

        // === Pagos por mes ===
        List<Integer> pagosPorMes = new ArrayList<>();
        for (int mes = 1; mes <= 12; mes++) {
            pagosPorMes.add(pagoRepository.countByMes(mes));
        }

        // === Ingresos por mes ===
        List<BigDecimal> ingresosPorMes = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));
        for (Object[] fila : pagoRepository.sumarMontoPorMes()) {
            if (fila[0] != null && fila[1] != null) {
                int mes = ((Number) fila[0]).intValue(); // ✅ conversión segura
                BigDecimal totalMes = (BigDecimal) fila[1];
                ingresosPorMes.set(mes - 1, totalMes);
            }
        }

        // === Total recaudado general ===
        BigDecimal totalRecaudado = pagoRepository.obtenerTotalRecaudado();
        if (totalRecaudado == null) {
            totalRecaudado = BigDecimal.ZERO; // ✅ evita NullPointerException
        }

        // === Métodos de pago ===
        Map<String, Long> metodosPago = new LinkedHashMap<>();
        for (Object[] fila : pagoRepository.contarPorMetodoPago()) {
            if (fila[0] != null && fila[1] != null) { // ✅ evita claves nulas
                metodosPago.put(String.valueOf(fila[0]), ((Number) fila[1]).longValue());
            }
        }

        // === Usuarios por rol ===
        Map<String, Long> usuariosPorRol = new LinkedHashMap<>();
        for (Object[] fila : usuarioRepository.contarPorRol()) {
            if (fila[0] != null && fila[1] != null) { // ✅ evita null en JSON
                usuariosPorRol.put(String.valueOf(fila[0]), ((Number) fila[1]).longValue());
            }
        }

        // === Cuotas por estado ===
        Map<String, Long> cuotasPorEstado = new LinkedHashMap<>();
        for (Object[] fila : cuotaMensualRepository.contarPorEstado()) {
            if (fila[0] != null && fila[1] != null) {
                cuotasPorEstado.put(String.valueOf(fila[0]), ((Number) fila[1]).longValue());
            }
        }

        return DashboardResponse.builder()
                .totalUsuarios(totalUsuarios)
                .totalCuotas(totalCuotas)
                .totalPagos(totalPagos)
                .totalRoles(totalRoles)
                .pagosPorMes(pagosPorMes)
                .ingresosPorMes(ingresosPorMes)
                .totalRecaudado(totalRecaudado)
                .metodosPago(metodosPago)
                .usuariosPorRol(usuariosPorRol)
                .cuotasPorEstado(cuotasPorEstado)
                .build();
    }
    @Override
public List<PagoDiaResponse> obtenerPagosPorDia(int mes, int anio) {
    List<Object[]> rows = pagoRepository.obtenerPagosPorDia(mes, anio);

    return rows.stream()
            .map(r -> new PagoDiaResponse(
                ((Number) r[0]).intValue(),
                ((Number) r[1]).longValue()
            ))
            .toList();
}

}
