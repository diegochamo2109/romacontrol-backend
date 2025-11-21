package com.romacontrol.romacontrol_v1.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad UsuarioCuota optimizada — evita recursividad con Usuario y CuotaMensual.
 */
@Entity
@Table(
    name = "usuario_cuota",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_usuario_cuota",
        columnNames = {"usuario_id", "cuota_id"}
    ),
    indexes = {
        @Index(name = "idx_uc_usuario", columnList = "usuario_id"),
        @Index(name = "idx_uc_cuota", columnList = "cuota_id"),
        @Index(name = "idx_uc_estado", columnList = "estado")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================
    // 🔹 Relación con Usuario
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_uc_usuario"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // evita ciclo Usuario ↔ UsuarioCuota
    private Usuario usuario;

    // ============================
    // 🔹 Relación con CuotaMensual
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "cuota_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_uc_cuota"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference // complementa con JsonManagedReference en CuotaMensual (si lo agregás)
    private CuotaMensual cuota;

    // ============================
    // 🔹 Estado y control de fechas
    // ============================
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 10, nullable = false)
    private UsuarioCuotaEstado estado;

    @Builder.Default
    @Column(name = "con_retraso", nullable = false)
    private boolean conRetraso = false;

    @NotNull
    @Column(name = "fecha_asignacion", nullable = false)
    private OffsetDateTime fechaAsignacion;

    @Column(name = "fecha_cambio_estado")
    private OffsetDateTime fechaCambioEstado;
}
