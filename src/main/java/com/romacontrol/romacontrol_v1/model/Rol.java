package com.romacontrol.romacontrol_v1.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="rol",
  uniqueConstraints=@UniqueConstraint(name="uk_rol_nombre", columnNames="nombre"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rol {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @NotBlank @Column(nullable=false, length=60) private String nombre;
  @Column(length=200) private String descripcion;
  @Column(name="fecha_creacion") private OffsetDateTime fechaCreacion;
  @Builder.Default @Column(nullable=false) private boolean activo = true;
}
