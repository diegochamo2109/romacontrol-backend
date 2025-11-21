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
 * Entidad EstadoPago — define el estado actual de un pago (ej.: PENDIENTE, PAGADO, ANULADO).
 */
@Entity
@Table(
    name = "estado_pago",
    uniqueConstraints = @UniqueConstraint(name = "uk_estado_pago_nombre", columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String nombre;

    // ============================
    // 🔹 Relación inversa (opcional, si luego listás pagos por estado)
    // ============================
    // @OneToMany(mappedBy = "estado")
    // @JsonIgnore
    // private Set<Pago> pagos;
}
