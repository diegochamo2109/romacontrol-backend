package com.romacontrol.romacontrol_v1.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="gasto",
  indexes={@Index(name="idx_gasto_tipo_fecha", columnList="tipo_gasto_id, fecha"),
           @Index(name="idx_gasto_registrado_por", columnList="registrado_por")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Gasto {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(length=180) private String descripcion;
  @NotNull @Column(name="monto", precision=12, scale=2, nullable=false) private BigDecimal monto;
  @NotNull @Column(name="fecha", nullable=false) private OffsetDateTime fecha;

  @ManyToOne(optional=false) @JoinColumn(name="tipo_gasto_id", nullable=false,
    foreignKey=@ForeignKey(name="fk_gasto_tipo"))
  private TipoGasto tipoGasto;

  @ManyToOne(optional=false) @JoinColumn(name="registrado_por", nullable=false,
    foreignKey=@ForeignKey(name="fk_gasto_usuario"))
  private Usuario registradoPor;
}
