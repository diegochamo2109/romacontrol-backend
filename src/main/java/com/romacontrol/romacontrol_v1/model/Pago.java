package com.romacontrol.romacontrol_v1.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Entidad Pago optimizada — evita recursividad con Usuario y CuotaMensual.
 */
@Entity
@Table(
    name = "pago",
    indexes = {
        @Index(name = "idx_pago_estado_fecha", columnList = "estado_pago_id, fecha_pago"),
        @Index(name = "idx_pago_cobrador_fecha", columnList = "cobrado_por, fecha_pago"),
        @Index(name = "idx_pago_metodo_fecha", columnList = "metodo_pago_id, fecha_pago")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================
    // 🔹 Relación con Usuario (socio que paga)
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pago_usuario"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // evita ciclo al listar pagos desde usuario
    private Usuario usuario;

    // ============================
    // 🔹 Relación con CuotaMensual
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "cuota_mensual_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pago_cuota"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference // complementa el JsonManagedReference en CuotaMensual
    private CuotaMensual cuotaMensual;

    // ============================
    // 🔹 Relación con Método de Pago
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "metodo_pago_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pago_metodo"))
    private MetodoPago metodoPago;

    // ============================
    // 🔹 Relación con Estado de Pago
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "estado_pago_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pago_estado"))
    private EstadoPago estado;

    // ============================
    // 🔹 Relación con Usuario (cobrador)
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "cobrado_por", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pago_cobrado_por"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // evita ciclo con Usuario
    private Usuario cobradoPor;

    // ============================
    // 🔹 Datos del pago
    // ============================
    @NotNull
    @Column(name = "fecha_pago", nullable = false)
    private OffsetDateTime fechaPago;

    @NotNull
    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Builder.Default
    @Column(name = "fuera_de_termino", nullable = false)
    private boolean fueraDeTermino = false;

    @Column(name = "motivo_anulacion", length = 255)
    private String motivoAnulacion;
}
