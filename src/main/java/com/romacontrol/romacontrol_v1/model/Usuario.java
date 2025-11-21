package com.romacontrol.romacontrol_v1.model;

import java.time.OffsetDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
 * Entidad Usuario optimizada para evitar recursividad e inconsistencias.
 */
@Entity
@Table(
    name = "usuario",
    uniqueConstraints = @UniqueConstraint(name = "uk_usuario_dni", columnNames = "dni"),
    indexes = {
        @Index(name = "idx_usuario_activo", columnList = "activo"),
        @Index(name = "idx_usuario_estado", columnList = "estado_usuario_id"),
        @Index(name = "idx_usuario_tipo_cuota", columnList = "tipo_cuota_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60)
    private String username;

    @NotBlank
    @Column(nullable = false, length = 15)
    private String dni;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String pin; // Hash (BCrypt)

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "persona_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_persona"))
    @JsonManagedReference // la parte "dueña" de la relación
    private Persona persona;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Rol> roles;

    @ManyToOne
    @JoinColumn(name = "estado_usuario_id",
            foreignKey = @ForeignKey(name = "fk_usuario_estado"))
    private EstadoUsuario estadoUsuario;

    @ManyToOne
    @JoinColumn(name = "tipo_cuota_id",
            foreignKey = @ForeignKey(name = "fk_usuario_tipo_cuota"))
    private TipoCuota tipoCuota;

    @ManyToOne
    @JoinColumn(name = "creado_por",
            foreignKey = @ForeignKey(name = "fk_usuario_creado_por"))
    @JsonBackReference // evita bucles en JSON
    private Usuario creadoPor;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private OffsetDateTime fechaModificacion;

    @OneToMany(mappedBy = "usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore // evita ciclo al listar usuarios
    private Set<UsuarioCuota> cuotasAsignadas;

    @ManyToOne
    @JoinColumn(name = "cuota_id",
            foreignKey = @ForeignKey(name = "fk_usuario_cuota_asignada"))
    private CuotaMensual cuotaAsignada;
}
