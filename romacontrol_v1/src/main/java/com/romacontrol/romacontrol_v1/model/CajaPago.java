package com.romacontrol.romacontrol_v1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="caja_pago",
  uniqueConstraints=@UniqueConstraint(name="uk_caja_pago_pago_unico", columnNames="pago_id"),
  indexes={@Index(name="idx_caja_pago_caja", columnList="caja_id"),
           @Index(name="idx_caja_pago_pago", columnList="pago_id")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CajaPago {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

  @ManyToOne(optional=false) @JoinColumn(name="caja_id", nullable=false,
    foreignKey=@ForeignKey(name="fk_caja_pago_caja"))
  private Caja caja;

  @ManyToOne(optional=false) @JoinColumn(name="pago_id", nullable=false,
    foreignKey=@ForeignKey(name="fk_caja_pago_pago"))
  private Pago pago;
}
