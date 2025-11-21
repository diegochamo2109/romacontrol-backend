package com.romacontrol.romacontrol_v1.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "cuota_mensual",
    indexes = {
        @Index(name = "idx_cuota_estado_tipo", columnList = "estado_cuota_id, tipo_cuota_id"),
        @Index(name = "idx_cuota_fechas", columnList = "fecha_limite, fecha_alta"),
        @Index(name = "idx_cuota_activa", columnList = "activa")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuotaMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false)
    private String descripcion;

    @NotNull
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal importe;

    // 📌 MANUAL — LA FECHA DE ALTA VIENE DEL FORMULARIO
    @NotNull
    @Column(name = "fecha_alta", nullable = false)
    private OffsetDateTime fechaAlta;

    // 📌 AUTOMÁTICA — FECHA REAL DE CREACIÓN
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    @NotNull
    @Column(name = "fecha_limite", nullable = false)
    private OffsetDateTime fechaLimite;

    @Column(name = "fecha_modificacion")
    private OffsetDateTime fechaModificacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estado_cuota_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cuota_estado"))
    private EstadoCuota estadoCuota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_cuota_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cuota_tipo"))
    private TipoCuota tipoCuota;

    @Builder.Default
    @Column(nullable = false)
    private boolean activa = true;

    // ============================
    // 🔹 Auditoría
    // ============================
    @ManyToOne
    @JoinColumn(name = "creado_por",
            foreignKey = @ForeignKey(name = "fk_cuota_creado_por"))
    @JsonBackReference
    private Usuario creadoPor;

    @ManyToOne
    @JoinColumn(name = "modificado_por",
            foreignKey = @ForeignKey(name = "fk_cuota_modificado_por"))
    @JsonBackReference
    private Usuario modificadoPor;

    // ============================
    // 🔹 Relación con Pagos
    // ============================
    @OneToMany(mappedBy = "cuotaMensual", cascade = CascadeType.ALL, orphanRemoval = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private Set<Pago> pagos;

    // ============================
    // 🔹 Normalización automática de Fechas
    // ============================
    @PrePersist
    protected void onCreate() {

        // NORMALIZA FECHA ALTA → 00:00:00
        if (fechaAlta != null) {
            fechaAlta = fechaAlta
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
        }

        // NORMALIZA FECHA LIMITE → 23:59:59
        if (fechaLimite != null) {
            fechaLimite = fechaLimite
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59)
                    .withNano(0);
        }

        this.fechaModificacion = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = OffsetDateTime.now();
    }
}
