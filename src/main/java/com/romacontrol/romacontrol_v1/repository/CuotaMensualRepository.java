package com.romacontrol.romacontrol_v1.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.EstadoCuota;

@Repository
public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, Long> {

    // ===========================================================
    // 📌 VALIDACIONES
    // ===========================================================
    boolean existsByDescripcion(String descripcion);

    // ===========================================================
    // 📌 VALIDAR SUPERPOSICIÓN DE FECHAS (CORRECTO)
    // ===========================================================
    @Query("""
        SELECT COUNT(c) > 0
        FROM CuotaMensual c
        WHERE (c.fechaAlta <= :fin AND c.fechaLimite >= :inicio)
    """)
    boolean existeRangoSuperpuesto(
            OffsetDateTime inicio,
            OffsetDateTime fin
    );

    // ===========================================================
    // 📌 OBTENER CUOTAS POR ESTADO
    // ===========================================================
    Optional<CuotaMensual> findByEstadoCuota_NombreIgnoreCase(String estado);
  

    List<CuotaMensual> findByEstadoCuota(EstadoCuota estado);

    // ===========================================================
    // 📌 CUOTA DEL MES — más reciente por fechaAlta
    // ===========================================================
    Optional<CuotaMensual>
    findFirstByEstadoCuota_NombreIgnoreCaseOrderByFechaAltaDesc(String estado);

    // ===========================================================
    // 📌 LISTADO DE CUOTAS ACTIVAS
    // ===========================================================
    List<CuotaMensual> findByActivaTrueOrderByFechaLimiteAsc();

    // ===========================================================
    // 📌 ESTADÍSTICAS
    // ===========================================================
    @Query("""
        SELECT e.nombre, COUNT(c)
        FROM CuotaMensual c
        JOIN c.estadoCuota e
        GROUP BY e.nombre
    """)
    List<Object[]> contarPorEstado();

}
