package com.romacontrol.romacontrol_v1.dto.respuesta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private Long totalUsuarios;
    private Long totalCuotas;
    private Long totalPagos;
    private Long totalRoles;

    private List<Integer> pagosPorMes;
    private Map<String, Long> metodosPago;
    private Map<String, Long> usuariosPorRol;
    private Map<String, Long> cuotasPorEstado;

    // 🟢 Nuevos campos
    private List<BigDecimal> ingresosPorMes;
    private BigDecimal totalRecaudado;
}
