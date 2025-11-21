

package com.romacontrol.romacontrol_v1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "rol_permiso",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_rol_permiso",
        columnNames = {"rol_id", "permiso_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rol_permiso_rol"))
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permiso_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_rol_permiso_permiso"))
    private Permiso permiso;

    @Column(nullable = false)
    private boolean habilitado = true;
}
