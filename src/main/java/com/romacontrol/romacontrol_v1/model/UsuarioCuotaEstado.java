package com.romacontrol.romacontrol_v1.model;

public enum UsuarioCuotaEstado {

    PENDIENTE("Pendiente de pago"),
    PAGADA("Pagada en término"),
    PAGADA_FUERA_DE_TERMINO("Pagada fuera de término"),
    ANULADA("Pago anulado");

    private final String descripcion;

    UsuarioCuotaEstado(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
