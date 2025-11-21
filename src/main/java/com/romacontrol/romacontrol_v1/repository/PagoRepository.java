package com.romacontrol.romacontrol_v1.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.romacontrol.romacontrol_v1.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    // ============================================================
    // 📌 ESTADÍSTICAS
    // ============================================================

    // Total de pagos en un mes específico
    @Query(value = "SELECT COUNT(*) FROM pago WHERE EXTRACT(MONTH FROM fecha_pago) = :mes", nativeQuery = true)
    int countByMes(@Param("mes") int mes);

    // Cantidad de pagos por método
    @Query("SELECT m.nombre, COUNT(p) FROM Pago p JOIN p.metodoPago m GROUP BY m.nombre")
    List<Object[]> contarPorMetodoPago();

    // Suma total cobrada por mes
    @Query(value = "SELECT EXTRACT(MONTH FROM fecha_pago) AS mes, COALESCE(SUM(monto), 0) " +
                   "FROM pago GROUP BY mes ORDER BY mes", nativeQuery = true)
    List<Object[]> sumarMontoPorMes();

    // Total general
    @Query(value = "SELECT COALESCE(SUM(monto), 0) FROM pago", nativeQuery = true)
    BigDecimal obtenerTotalRecaudado();


    // ============================================================
    // 📌 VALIDACIONES
    // ============================================================

    Optional<Pago> findByUsuarioIdAndCuotaMensualId(Long usuarioId, Long cuotaMensualId);

    boolean existsByCuotaMensual_Id(Long cuotaId);


    // ============================================================
    // 📌 MÓDULO CAJA — PAGOS DEL DÍA
    // ============================================================

    // Cierre Parcial → pagos cobrados por el usuario logueado
    @Query("""
           SELECT p FROM Pago p
           WHERE DATE(p.fechaPago) = :fecha
           AND p.cobradoPor.dni = :dni
           """)
    List<Pago> findByFechaPagoAndCobradoPorDni(
            @Param("fecha") LocalDate fecha,
            @Param("dni") String dni);


    // Cierre Completo → todos los pagos del día
    @Query("""
           SELECT p FROM Pago p
           WHERE DATE(p.fechaPago) = :fecha
           """)
    List<Pago> findPagosDelDia(@Param("fecha") LocalDate fecha);


    // ============================================================
    // 📌 HISTORIAL DE PAGOS DEL SOCIO
    // ============================================================

    List<Pago> findByUsuarioId(Long usuarioId);

    List<Pago> findByUsuario_IdOrderByFechaPagoDesc(Long usuarioId);

    @Query(value = """
    SELECT EXTRACT(DAY FROM p.fecha_pago) AS dia,
           COUNT(*) AS cantidad
    FROM pago p
    WHERE EXTRACT(MONTH FROM p.fecha_pago) = :mes
      AND EXTRACT(YEAR FROM p.fecha_pago) = :anio
    GROUP BY dia
    ORDER BY dia
""", nativeQuery = true)
List<Object[]> obtenerPagosPorDia(int mes, int anio);



    // ============================================================
    // 🔸 NUEVOS MÉTODOS PARA EL MÓDULO "CAJA"
    // ============================================================

    // Pagos del usuario logueado (por username)
    @Query("""
           SELECT p FROM Pago p
           WHERE DATE(p.fechaPago) = :fecha
           AND p.cobradoPor.username = :username
           """)
    List<Pago> findByFechaPagoAndCobradoPorUsername(
            @Param("fecha") LocalDate fecha,
            @Param("username") String username);

    // Cierre Completo → mismo propósito que findPagosDelDia
    @Query("SELECT p FROM Pago p WHERE DATE(p.fechaPago) = :fecha")
    List<Pago> findByFechaPago(@Param("fecha") LocalDate fecha);

}
