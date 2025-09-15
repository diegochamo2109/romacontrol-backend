
package com.romacontrol.romacontrol_v1.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="cuota_mensual",
  indexes={@Index(name="idx_cuota_estado_tipo", columnList="estado_cuota_id, tipo_cuota_id"),
           @Index(name="idx_cuota_fechas", columnList="fecha_limite, fecha_alta"),
           @Index(name="idx_cuota_activa", columnList="activa")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CuotaMensual {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(length=120, nullable=false) private String descripcion;
  @NotNull @Column(name="importe", precision=12, scale=2, nullable=false) private BigDecimal importe;
  @NotNull @Column(name="fecha_alta", nullable=false) private OffsetDateTime fechaAlta;
  @NotNull @Column(name="fecha_limite", nullable=false) private OffsetDateTime fechaLimite;
  @ManyToOne(optional=false) @JoinColumn(name="estado_cuota_id", nullable=false,
    foreignKey=@ForeignKey(name="fk_cuota_estado")) private EstadoCuota estadoCuota;
  @ManyToOne(optional=false) @JoinColumn(name="tipo_cuota_id", nullable=false,
    foreignKey=@ForeignKey(name="fk_cuota_tipo")) private TipoCuota tipoCuota;
  @Builder.Default @Column(nullable=false) private boolean activa = false;
  @ManyToOne @JoinColumn(name="creado_por", foreignKey=@ForeignKey(name="fk_cuota_creado_por"))
  private Usuario creadoPor;
  @OneToMany(mappedBy = "cuota")
  private Set<UsuarioCuota> usuariosAsignados;
  public boolean isActiva() {
    return activa;
}
public String getDescripcion() {
    return descripcion;
}

  
}
