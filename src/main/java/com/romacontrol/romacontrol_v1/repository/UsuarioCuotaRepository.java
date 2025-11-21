package com.romacontrol.romacontrol_v1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.UsuarioCuota;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;

@Repository
public interface UsuarioCuotaRepository extends JpaRepository<UsuarioCuota, Long> {

    // ============================================
    // 🔵 ASIGNAR MASIVAMENTE A USUARIOS ACTIVOS
    // ============================================
    @Modifying
    @Query(value = """
        INSERT INTO usuario_cuota (usuario_id, cuota_id, fecha_asignacion, estado, con_retraso)
        SELECT u.id, :cuotaId, NOW(), 'PENDIENTE', false
        FROM usuario u
        WHERE u.activo = true
          AND NOT EXISTS (
            SELECT 1 FROM usuario_cuota uc
             WHERE uc.usuario_id = u.id AND uc.cuota_id = :cuotaId
          )
        """, nativeQuery = true)
    int asignarMasivoATodosActivos(@Param("cuotaId") Long cuotaId);


    // ============================================
    // 🔵 LISTAR USUARIOS DE UNA CUOTA
    // ============================================
    @Query("""
        SELECT uc 
        FROM UsuarioCuota uc
        JOIN FETCH uc.usuario u
        JOIN FETCH u.persona p
        WHERE uc.cuota.id = :cuotaId
          AND (:estado IS NULL OR uc.estado = :estado)
        ORDER BY p.apellido, p.nombre
        """)
    List<UsuarioCuota> findUsuariosDeCuota(@Param("cuotaId") Long cuotaId,
                                           @Param("estado") UsuarioCuotaEstado estado);


    // ============================================
    // 🔵 VALIDACIONES INDIVIDUALES
    // ============================================
    Optional<UsuarioCuota> findByUsuarioIdAndCuotaId(Long usuarioId, Long cuotaId);

    boolean existsByUsuarioIdAndCuotaId(Long usuarioId, Long cuotaId);


    // ============================================
    // 🔵 BUSCAR POR DNI + ESTADOS
    // ============================================
    List<UsuarioCuota> findByUsuario_DniAndEstadoIn(String dni, List<UsuarioCuotaEstado> estados);


    // ============================================
    // 🔵 BUSCAR POR ID + ESTADOS
    // ============================================
    List<UsuarioCuota> findByUsuarioIdAndEstadoIn(Long usuarioId, List<UsuarioCuotaEstado> estados);



    // ============================================================
    // 🆕 NUEVO — REGRESAN TRUE/FALSE PARA LOGICA DE REACTIVACIÓN
    // ============================================================

    // 1) Tiene pagos registrados → estado != PENDIENTE
    @Query("""
        SELECT COUNT(uc) > 0
        FROM UsuarioCuota uc
        WHERE uc.usuario.id = :usuarioId
          AND uc.estado <> com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado.PENDIENTE
    """)
    boolean tienePagos(@Param("usuarioId") Long usuarioId);


    // 2) Tiene cuotas pendientes → estado = PENDIENTE
    @Query("""
        SELECT COUNT(uc) > 0
        FROM UsuarioCuota uc
        WHERE uc.usuario.id = :usuarioId
          AND uc.estado = com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado.PENDIENTE
    """)
    boolean tienePendientes(@Param("usuarioId") Long usuarioId);
    long countByCuotaId(Long cuotaId);

long countByCuotaIdAndEstado(Long cuotaId, UsuarioCuotaEstado estado);

}
