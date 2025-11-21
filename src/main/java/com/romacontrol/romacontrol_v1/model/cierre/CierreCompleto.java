package com.romacontrol.romacontrol_v1.model.cierre;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
@Table(name = "cierre_completo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CierreCompleto {   

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha y hora real en la que el ADMIN realiza el cierre
    @Column(name = "fecha_hora_cierre", nullable = false)
    private OffsetDateTime fechaHoraCierre;

    // Total general del día (suma de todos los cierre_parcial)
    @Column(name = "total_general", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalGeneral;

    // Usuario ADMIN que ejecutó el cierre completo
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "cerrado_por",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cierre_completo_usuario")
    )
    private Usuario cerradoPor;

    // Observaciones opcionales (puede quedar NULL)
    @Column(length = 255)
    private String observaciones;
}
