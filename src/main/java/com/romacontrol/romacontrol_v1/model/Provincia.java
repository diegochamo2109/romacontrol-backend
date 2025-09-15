package com.romacontrol.romacontrol_v1.model;

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

@Entity @Table(name="provincia",
  uniqueConstraints=@UniqueConstraint(name="uk_provincia_nombre", columnNames="nombre"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Provincia {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @NotBlank @Column(nullable=false, length=80) private String nombre;
}
