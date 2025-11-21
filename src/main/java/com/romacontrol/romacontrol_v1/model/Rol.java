

package com.romacontrol.romacontrol_v1.model;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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

@Entity
@Table(
    name = "rol",
    uniqueConstraints = @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private OffsetDateTime fechaCreacion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    // ==========================================
    // 🔹 RELACIÓN CON ROL_PERMISO
    // ==========================================
    @Builder.Default
    @OneToMany(
            mappedBy = "rol",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<RolPermiso> rolPermisos = new LinkedHashSet<>();

    // ====111111111111111111111111111111111111111111111======================================
    // 🔹 RELACIÓN CON USUARIO (ya la tenías)
    // ==========================================
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Usuario> usuarios;

    // ==========================================
    // 🔹 HELPERS
    // ==========================================
    public void agregarRolPermiso(RolPermiso rp) {
        rolPermisos.add(rp);
        rp.setRol(this);
    }

    public void quitarRolPermiso(RolPermiso rp) {
        rolPermisos.remove(rp);
        rp.setRol(null);
    }
}
