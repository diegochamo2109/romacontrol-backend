package com.romacontrol.romacontrol_v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.Pago;

@Repository
public interface ReporteRepository extends JpaRepository<Pago, Long> {

    // ===============================================================
    // 🔹 1. HISTORIAL DE PAGOS DETALLADO
    // ===============================================================
    @Query(value = """
        SELECT 
            p.fecha_pago AS fechaPago,
            u.dni AS dni,
            CONCAT(per.nombre, ' ', per.apellido) AS nombreCompleto,
            c.descripcion AS cuotaDescripcion,
            c.fecha_limite AS fechaVencimiento,
            p.monto AS monto,
            mp.nombre AS metodoPago,
            ep.nombre AS estado,
            p.fuera_de_termino AS conRetraso,
            CONCAT(cobPer.nombre, ' ', cobPer.apellido) AS cobradoPor,
            COALESCE(p.motivo_anulacion, '') AS observacion
        FROM pago p
        INNER JOIN usuario u ON p.usuario_id = u.id
        INNER JOIN persona per ON u.persona_id = per.id
        INNER JOIN cuota_mensual c ON p.cuota_mensual_id = c.id
        INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id
        INNER JOIN estado_pago ep ON p.estado_pago_id = ep.id
        LEFT JOIN usuario cob ON p.cobrado_por = cob.id
        LEFT JOIN persona cobPer ON cob.persona_id = cobPer.id
        WHERE 
            (:dni IS NULL OR u.dni = :dni)
            AND (
                :estado IS NULL 
                OR TRIM(:estado) = '' 
                OR ep.nombre = :estado
            )
            AND (:desde IS NULL OR p.fecha_pago >= CAST(:desde AS timestamp))
            AND (:hasta IS NULL OR p.fecha_pago <= CAST(:hasta AS timestamp))
        ORDER BY p.fecha_pago DESC
        """, nativeQuery = true)
    List<Object[]> obtenerHistorialPagos(
        @Param("dni") String dni,
        @Param("desde") String desde,
        @Param("hasta") String hasta,
        @Param("estado") String estado
    );



    // ===============================================================
    // 🔹 2. RESUMEN POR CUOTAS (Agrupado)
    // ===============================================================
    @Query(value = """
        SELECT 
            c.descripcion AS descripcionCuota,
            c.importe AS importe,
            COUNT(DISTINCT u.id) AS totalUsuariosAsignados,
            COUNT(p.id) AS totalPagosRealizados,
            SUM(CASE WHEN p.fuera_de_termino = true THEN 1 ELSE 0 END) AS pagosConRetraso,
            COALESCE(SUM(p.monto), 0) AS totalRecaudado
        FROM cuota_mensual c
        LEFT JOIN pago p ON c.id = p.cuota_mensual_id
        LEFT JOIN usuario u ON p.usuario_id = u.id
        GROUP BY c.id, c.descripcion, c.importe
        ORDER BY c.fecha_limite DESC
        """, nativeQuery = true)
    List<Object[]> obtenerResumenCuotas();


    // ===============================================================
// 🔹 3. CUOTAS PENDIENTES O VENCIDAS (para historial completo)
// ===============================================================
@Query(value = """
    SELECT 
        uc.fecha_asignacion AS fechaLimite,
        u.dni AS dni,
        CONCAT(per.nombre, ' ', per.apellido) AS nombreCompleto,
        c.descripcion AS cuotaDescripcion,
        uc.fecha_asignacion AS fechaVencimiento,
        c.importe AS monto,
        '—' AS metodoPago,
        uc.estado AS estado,
        FALSE AS conRetraso,
        '—' AS cobradoPor,
        NULL AS observacion
    FROM usuario_cuota uc
    INNER JOIN usuario u ON uc.usuario_id = u.id
    INNER JOIN persona per ON u.persona_id = per.id
    INNER JOIN cuota_mensual c ON uc.cuota_id = c.id
    WHERE (:dni IS NULL OR u.dni = :dni)
      AND (:estado IS NULL OR uc.estado = :estado)
""", nativeQuery = true)
List<Object[]> obtenerCuotasPendientesOVencidas(
        @Param("dni") String dni,
        @Param("estado") String estado);

}

