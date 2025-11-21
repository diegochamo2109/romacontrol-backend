package com.romacontrol.romacontrol_v1.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.Gasto;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    // 🔹 Listar solo los gastos activos
    List<Gasto> findByActivoTrue();

    // 🔹 Listar solo los inactivos (si se quiere mostrar aparte)
    List<Gasto> findByActivoFalse();

    // 🔹 Buscar por tipo de gasto (filtrado adicional)
    List<Gasto> findByTipoGasto_Id(Long tipoGastoId);

    // 🔹 Buscar por usuario que lo registró (DNI)
    List<Gasto> findByRegistradoPor_Dni(String dni);

    // 🔹 Buscar entre fechas (para filtros del módulo)
    List<Gasto> findByFechaBetween(OffsetDateTime desde, OffsetDateTime hasta);

    // 🔹 Combinado: activos por tipo de gasto (útil para gestión)
    List<Gasto> findByActivoTrueAndTipoGasto_Id(Long tipoGastoId);
}
