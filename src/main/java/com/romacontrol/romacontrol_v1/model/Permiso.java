package com.romacontrol.romacontrol_v1.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
 * Permiso individual que representa acceso a una página o módulo.
 * Ejemplo de ruta: "pages/usuario/gestionar-usuario.html"
 */
@Entity
@Table(
    name = "permiso",
    uniqueConstraints = @UniqueConstraint(name = "uk_permiso_ruta", columnNames = "ruta")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ruta o identificador del módulo. */
    @NotBlank
    @Column(nullable = false, length = 200)
    private String ruta;

    @Column(length = 200)
    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;
    @Column(nullable = false, length = 100)
    private String titulo;


    // ==========================================
    // 🔹 RELACIÓN CON ROL_PERMISO
    // ==========================================
    @Builder.Default
    @OneToMany(mappedBy = "permiso", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<RolPermiso> rolPermisos = new LinkedHashSet<>();
}
