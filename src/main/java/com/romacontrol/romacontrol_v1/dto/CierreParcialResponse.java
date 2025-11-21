package com.romacontrol.romacontrol_v1.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.romacontrol.romacontrol_v1.model.Pago;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CierreParcialResponse {
    private Long id;
    private String dni;
    private String socio;
    private String cuotaDescripcion;
    private BigDecimal importe;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPago;

    public static CierreParcialResponse fromEntity(Pago p) {
        String nombreCompleto = "";
        String dni = "";
        String cuota = "";
        String metodo = "Desconocido";
        String estado = "-";

        if (p.getUsuario() != null && p.getUsuario().getPersona() != null) {
            var persona = p.getUsuario().getPersona();
            nombreCompleto = String.format("%s %s",
                    persona.getNombre() != null ? persona.getNombre() : "",
                    persona.getApellido() != null ? persona.getApellido() : "").trim();
            dni = p.getUsuario().getDni();

        }

        if (p.getCuotaMensual() != null) {
            cuota = p.getCuotaMensual().getDescripcion();
        }

        if (p.getMetodoPago() != null) {
            metodo = p.getMetodoPago().getDescripcion();
        }

        if (p.getEstado() != null) {
            estado = p.getEstado().getNombre();
        }

        return CierreParcialResponse.builder()
                .id(p.getId())
                .dni(dni)
                .socio(nombreCompleto)
                .cuotaDescripcion(cuota)
                .importe(p.getMonto())
                .metodoPago(metodo)
                .estado(estado)
                .fechaPago(p.getFechaPago().toLocalDateTime()) // si usás OffsetDateTime
                .build();
    }
}
