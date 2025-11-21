package com.romacontrol.romacontrol_v1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad Localidad — representa una localidad o ciudad perteneciente a una provincia.
 */
@Entity
@Table(
    name = "localidad",
    indexes = @Index(name = "idx_localidad_provincia", columnList = "provincia_id"),
    uniqueConstraints = @UniqueConstraint(
        name = "uk_localidad_nombre_prov",
        columnNames = {"nombre", "provincia_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Localidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @ManyToOne(optional = false)
    @JoinColumn(
        name = "provincia_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_localidad_provincia")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference // complementa la relación bidireccional con Provincia
    private Provincia provincia;

    // ============================
    // 🔹 Relación inversa (si luego querés listar personas por localidad)
    // ============================
    // @OneToMany(mappedBy = "localidad")
    // @JsonIgnore
    // private Set<Persona> personas;
}
