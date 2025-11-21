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
 * Entidad TipoCuota optimizada — representa el tipo o categoría de cuota (por ejemplo: mensual, anual, promocional).
 */
@Entity
@Table(
    name = "tipo_cuota",
    uniqueConstraints = @UniqueConstraint(name = "uk_tipo_cuota_nombre", columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nombre;

    // ============================
    // 🔹 Relaciones inversas (si luego se agregan CuotaMensual o Usuarios)
    // ============================
    // @OneToMany(mappedBy = "tipoCuota")
    // @JsonIgnore
    // private Set<CuotaMensual> cuotas;
}
