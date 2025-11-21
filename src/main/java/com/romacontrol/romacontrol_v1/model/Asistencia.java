package com.romacontrol.romacontrol_v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad Asistencia — registra el ingreso y salida diaria de un usuario (socio o empleado).
 */
@Entity
@Table(name = "asistencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================
    // 🔹 Relación con Usuario
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_asistencia_usuario"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // evita recursividad con Usuario
    private Usuario usuario;

    // ============================
    // 🔹 Datos de registro
    // ============================
    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    @Column(name = "hora_registro", nullable = false)
    private LocalTime horaRegistro;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "intento_extra", nullable = false)
    private boolean intentoExtra = false; // true si intentó más de una vez el mismo día

    @Column(name = "observacion", length = 255)
    private String observacion; // comentario opcional o nota de auditoría

    @Column(name = "salida_generada_automatica", nullable = false)
    private Boolean salidaGeneradaAutomatica = false;

    // ============================
    // 🔹 Lógica automática
    // ============================
    @PrePersist
    public void prePersist() {
        if (salidaGeneradaAutomatica == null) {
            salidaGeneradaAutomatica = false;
        }
        if (fechaRegistro == null) {
            fechaRegistro = LocalDate.now();
        }
        if (horaRegistro == null) {
            horaRegistro = LocalTime.now();
        }
    }

    // Getter adicional para compatibilidad con servicios existentes
    public boolean isSalidaGeneradaAutomatica() {
        return Boolean.TRUE.equals(this.salidaGeneradaAutomatica);
    }
}
