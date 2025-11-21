package com.romacontrol.romacontrol_v1.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
 * Entidad Provincia — representa una provincia dentro del sistema RomaControl.
 * Ejemplo: Santa Fe, Buenos Aires, Córdoba, etc.
 */
@Entity
@Table(
    name = "provincia",
    uniqueConstraints = @UniqueConstraint(name = "uk_provincia_nombre", columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provincia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String nombre;

    // ============================
    // 🔹 Relación con Localidad
    // ============================
    @OneToMany(mappedBy = "provincia", cascade = CascadeType.ALL, orphanRemoval = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference // empareja con @JsonBackReference en Localidad
    private Set<Localidad> localidades;
}


