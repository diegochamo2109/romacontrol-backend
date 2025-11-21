

package com.romacontrol.romacontrol_v1.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad RegistroTerminal — registra la apertura y cierre de una terminal de asistencia o caja.
 * Permite auditar quién la abrió, quién la cerró y cuándo ocurrió.
 */
@Entity
@Table(name = "registro_terminal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroTerminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================
    // 🔹 Usuarios responsables
    // ============================
    @ManyToOne(optional = false)
    @JoinColumn(name = "administrador_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_registro_terminal_admin"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // evita recursividad con Usuario
    private Usuario administrador;

    @ManyToOne
    @JoinColumn(name = "cerrada_por_id",
            foreignKey = @ForeignKey(name = "fk_registro_terminal_cierre"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Usuario cerradaPor;

    // ============================
    // 🔹 Fechas y estados
    // ============================
    @Column(name = "fecha_hora_apertura", nullable = false)
    private LocalDateTime fechaHoraApertura;

    @Column(name = "fecha_hora_cierre")
    private LocalDateTime fechaHoraCierre;

    @Builder.Default
    @Column(name = "terminal_abierta", nullable = false)
    private boolean terminalAbierta = true;

    // ============================
    // 🔹 Observaciones
    // ============================
    @Column(length = 255)
    private String observacion;
}
