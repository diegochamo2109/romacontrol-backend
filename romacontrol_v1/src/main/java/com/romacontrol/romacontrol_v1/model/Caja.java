package com.romacontrol.romacontrol_v1.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="caja",
  indexes=@Index(name="idx_caja_usuario", columnList="usuario_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Caja {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

  @ManyToOne(optional=false) @JoinColumn(name="usuario_id", nullable=false,
    foreignKey=@ForeignKey(name="fk_caja_usuario"))
  private Usuario usuario;

  @NotNull @Column(name="apertura_ts", nullable=false) private OffsetDateTime aperturaTs;
  @Column(name="cierre_ts") private OffsetDateTime cierreTs;
  @Column(name="total_importe", precision=12, scale=2) private BigDecimal totalImporte;
  @Column(name="total_pagos") private Integer totalPagos;
  @Column(length=255) private String observaciones;

  @OneToMany(mappedBy="caja", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<CajaPago> pagos;
}
