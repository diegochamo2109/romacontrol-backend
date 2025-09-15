package com.romacontrol.romacontrol_v1.model;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="localidad",
  indexes=@Index(name="idx_localidad_provincia", columnList="provincia_id"),
  uniqueConstraints=@UniqueConstraint(name="uk_localidad_nombre_prov", columnNames={"nombre","provincia_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Localidad {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @NotBlank @Column(nullable=false, length=120) private String nombre;
  @ManyToOne(optional=false) @JoinColumn(name="provincia_id", nullable=false,
    foreignKey=@ForeignKey(name="fk_localidad_provincia"))
  private Provincia provincia;
}
