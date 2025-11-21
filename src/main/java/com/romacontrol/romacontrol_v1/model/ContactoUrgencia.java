package com.romacontrol.romacontrol_v1.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad ContactoUrgencia optimizada — evita recursividad con Persona.
 */
@Entity
@Table(
    name = "contacto_urgencia",
    indexes = @Index(name = "idx_contacto_persona", columnList = "persona_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoUrgencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String apellido;

    @Column(length = 8)
    private String telefonoArea;

    @Column(length = 20)
    private String telefonoNumero;

    @Column(length = 60)
    private String relacion;

    @ManyToOne
    @JoinColumn(name = "localidad_id",
            foreignKey = @ForeignKey(name = "fk_contacto_localidad"))
    private Localidad localidad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "persona_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_contacto_persona"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference // complementa el @JsonBackReference en Persona
    private Persona persona;
}
