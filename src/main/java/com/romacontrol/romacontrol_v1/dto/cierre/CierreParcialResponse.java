package com.romacontrol.romacontrol_v1.dto.cierre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.romacontrol.romacontrol_v1.model.cierre.CierreParcial;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CierreParcialResponse {

    private Long id;
    private LocalDate fecha;
    private String usuarioNombre;
    private BigDecimal totalDelDia;
    private LocalDateTime fechaHoraCierre;

    public static CierreParcialResponse fromEntity(CierreParcial cp) {

        String nombreCompleto =
                (cp.getUsuario() != null && cp.getUsuario().getPersona() != null)
                        ? cp.getUsuario().getPersona().getNombre() + " " + cp.getUsuario().getPersona().getApellido()
                        : "Desconocido";

        return CierreParcialResponse.builder()
                .id(cp.getId())
                .fecha(cp.getFecha())
                .usuarioNombre(nombreCompleto)
                .totalDelDia(cp.getTotalDelDia())
                .fechaHoraCierre(cp.getFechaHoraCierre())
                .build();
    }
}
