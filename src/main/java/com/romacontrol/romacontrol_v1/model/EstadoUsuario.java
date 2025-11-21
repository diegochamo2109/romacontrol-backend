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

/**
 * Entidad EstadoUsuario — representa el estado actual de un usuario dentro del sistema.
 * 
 * Ejemplos:  
 * 🔹 ACTIVO → puede acceder al sistema.  
 * 🔹 INACTIVO → no tiene acceso temporal o permanente.  
 * 🔹 SUSPENDIDO → bloqueado por un administrador.
 */
@Entity
@Table(
    name = "estado_usuario",
    uniqueConstraints = @UniqueConstraint(name = "uk_estado_usuario_nombre", columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 40)
    private String nombre;

    // ============================
    // 🔹 Relación inversa (si luego listás usuarios por estado)
    // ============================
    // @OneToMany(mappedBy = "estadoUsuario")
    // @JsonIgnore
    // private Set<Usuario> usuarios;
}
