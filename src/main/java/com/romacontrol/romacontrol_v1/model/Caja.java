package com.romacontrol.romacontrol_v1.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad Caja — representa una sesión de caja, controlando las operaciones de pagos
 * realizadas por un usuario (apertura, cierre, montos y observaciones).
 */
@Entity
@Table(
    name = "caja",
    indexes = @Index(name = "idx_caja_usuario", columnList = "usuario_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================
    // 🔹 Usuario responsable
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_caja_usuario"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference // evita ciclo con Usuario si se agrega la lista inversa
    private Usuario usuario;

    // ============================
    // 🔹 Datos de apertura y cierre
    // ============================
    @NotNull
    @Column(name = "apertura_ts", nullable = false)
    private OffsetDateTime aperturaTs;

    @Column(name = "cierre_ts")
    private OffsetDateTime cierreTs;

    // ============================
    // 🔹 Resumen de operaciones
    // ============================
    @Column(name = "total_importe", precision = 12, scale = 2)
    private BigDecimal totalImporte;

    @Column(name = "total_pagos")
    private Integer totalPagos;

    @Column(length = 255)
    private String observaciones;

    // ============================
    // 🔹 Relación con los pagos registrados en la caja
    // ============================
    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference // empareja con @JsonBackReference en CajaPago
    private List<CajaPago> pagos;
}
