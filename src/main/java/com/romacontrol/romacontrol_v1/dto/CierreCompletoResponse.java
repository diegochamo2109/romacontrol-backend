package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.romacontrol.romacontrol_v1.model.Pago;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CierreCompletoResponse {

    private String cobradoPor;
    private BigDecimal totalRecaudado;
    private List<String> sociosCobrados;

    public static CierreCompletoResponse fromGroup(String cobradoPor, List<Pago> pagos) {
        // 🔹 Calcular total recaudado
        BigDecimal total = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 🔹 Listar nombres de socios cobrados (nombre + apellido)
        List<String> socios = pagos.stream()
                .map(p -> {
                    if (p.getUsuario() != null && p.getUsuario().getPersona() != null) {
                        var persona = p.getUsuario().getPersona();
                        return String.format("%s %s",
                                persona.getNombre() != null ? persona.getNombre() : "",
                                persona.getApellido() != null ? persona.getApellido() : "").trim();
                    } else {
                        return "Desconocido";
                    }
                })
                .collect(Collectors.toList());

        // 🔹 Construir DTO
        return CierreCompletoResponse.builder()
                .cobradoPor(cobradoPor)
                .totalRecaudado(total)
                .sociosCobrados(socios)
                .build();
    }
}
