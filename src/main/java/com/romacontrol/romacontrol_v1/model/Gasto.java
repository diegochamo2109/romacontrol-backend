package com.romacontrol.romacontrol_v1.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
 * Entidad Gasto — representa un egreso registrado en el sistema RomaControl.
 * Cada gasto tiene un tipo, un monto, una fecha y un usuario responsable de su registro.
 */
@Entity
@Table(
    name = "gasto",
    indexes = {
        @Index(name = "idx_gasto_tipo_fecha", columnList = "tipo_gasto_id, fecha"),
        @Index(name = "idx_gasto_registrado_por", columnList = "registrado_por"),
        @Index(name = "idx_gasto_activo", columnList = "activo")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 180)
    private String descripcion;

    @NotNull
    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    // 🔹 Fecha contable del gasto (fecha de carga)
    @NotNull
    @Column(name = "fecha", nullable = false)
    private OffsetDateTime fecha;

    // 🔹 Fecha en que fue registrado efectivamente en el sistema
    @NotNull
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    // ============================
    // 🔹 Relación con Tipo de Gasto
    // ============================
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "tipo_gasto_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_gasto_tipo")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private TipoGasto tipoGasto;

    // ============================
    // 🔹 Usuario que registró el gasto
    // ============================
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
        name = "registrado_por",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_gasto_usuario")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Usuario registradoPor;

    // ============================
    // 🔹 Eliminación lógica
    // ============================
    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    // ============================
    // 🔹 Inicialización automática
    // ============================
    @PrePersist
    protected void onCreate() {
        OffsetDateTime ahoraBuenosAires = OffsetDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        if (this.fecha == null) {
            this.fecha = ahoraBuenosAires;
        }
        if (this.fechaCreacion == null) {
            this.fechaCreacion = ahoraBuenosAires;
        }
        if (this.activo == null) {
            this.activo = true;
        }
    }
}
