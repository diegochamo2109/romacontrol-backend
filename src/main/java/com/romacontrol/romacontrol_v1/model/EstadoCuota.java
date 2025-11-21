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
 * Entidad EstadoCuota — representa el estado actual de una cuota mensual (por ejemplo: Pendiente, Pagada, Vencida).
 */
@Entity
@Table(
    name = "estado_cuota",
    uniqueConstraints = @UniqueConstraint(name = "uk_estado_cuota_nombre", columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoCuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 40)
    private String nombre;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activa = true;

    // ============================
    // 🔹 Relación inversa (si luego se desea listar cuotas por estado)
    // ============================
    // @OneToMany(mappedBy = "estadoCuota")
    // @JsonIgnore
    // private Set<CuotaMensual> cuotas;
}
