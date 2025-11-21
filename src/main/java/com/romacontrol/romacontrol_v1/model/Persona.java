package com.romacontrol.romacontrol_v1.model;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad Persona optimizada — evita recursividad con ContactoUrgencia y Usuario.
 */
@Entity
@Table(
    name = "persona",
    indexes = {
        @Index(name = "idx_persona_apellido", columnList = "apellido"),
        @Index(name = "idx_persona_nombre", columnList = "nombre")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String apellido;

    @Past
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 120)
    private String domicilio;

    @Column(length = 8)
    private String telefonoArea;

    @Column(length = 20)
    private String telefonoNumero;

    @Email
    @Column(length = 120)
    private String email;

    // 📷 Foto almacenada en PostgreSQL como bytea
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "foto_perfil")
    @JsonIgnore // evita incluir bytes en JSON por defecto
    private byte[] fotoPerfil;

    @ManyToOne
    @JoinColumn(name = "genero_id",
            foreignKey = @ForeignKey(name = "fk_persona_genero"))
    private Genero genero;

    @ManyToOne
    @JoinColumn(name = "localidad_id",
            foreignKey = @ForeignKey(name = "fk_persona_localidad"))
    private Localidad localidad;

    @OneToOne(mappedBy = "persona")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference // evita ciclo con ContactoUrgencia
    private ContactoUrgencia contactoUrgencia;
}
