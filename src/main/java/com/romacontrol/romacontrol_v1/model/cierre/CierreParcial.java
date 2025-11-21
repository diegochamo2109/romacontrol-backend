package com.romacontrol.romacontrol_v1.model.cierre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.romacontrol.romacontrol_v1.model.Usuario;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cierre_parcial")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CierreParcial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha del cierre (día que se está cerrando)
    @Column(nullable = false)
    private LocalDate fecha;

    // Quién hizo el cierre (profesor o admin)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cierre_parcial_usuario"))
    private Usuario usuario;

    // Total de pagos cobrados ese día
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDelDia;

    // Fecha y hora exacta del cierre
    @Column(nullable = false)
    private LocalDateTime fechaHoraCierre;
}
