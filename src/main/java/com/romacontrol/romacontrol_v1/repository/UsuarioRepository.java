package com.romacontrol.romacontrol_v1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.romacontrol.romacontrol_v1.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  // ============================================================
  // 📊 Estadísticas por rol
  // ============================================================
  @Query("""
         SELECT r.descripcion, COUNT(u)
         FROM Usuario u JOIN u.roles r
         GROUP BY r.descripcion
         """)
  List<Object[]> contarPorRol();   

  // ============================================================
  // 🔍 Búsquedas y consultas básicas
  // ============================================================
  List<Usuario> findByActivoTrue();
  List<Usuario> findByActivoFalse();

  Optional<Usuario> findByDni(String dni);
  boolean existsByDni(String dni);

  @Query("SELECT u.id FROM Usuario u WHERE u.dni = :dni")
  Optional<Long> findIdByDni(@Param("dni") String dni);

  // ============================================================
  // 🔍 Consultas con FETCH para evitar N+1
  // ============================================================
  @Query("""
         SELECT DISTINCT u
         FROM Usuario u
         LEFT JOIN FETCH u.persona p
         LEFT JOIN FETCH u.roles r
         LEFT JOIN FETCH u.creadoPor c
         LEFT JOIN FETCH c.persona cp
         """)
  List<Usuario> findAllWithPersonaRolCreador();

  @Query("""
         SELECT DISTINCT u
         FROM Usuario u
         LEFT JOIN FETCH u.persona p
         LEFT JOIN FETCH u.roles r
         LEFT JOIN FETCH u.creadoPor c
         LEFT JOIN FETCH c.persona cp
         WHERE u.activo = true
         """)
  List<Usuario> findActiveWithPersonaRolCreador();

  @Query("""
         SELECT u
         FROM Usuario u
         LEFT JOIN FETCH u.persona p
         LEFT JOIN FETCH u.roles r
         LEFT JOIN FETCH u.creadoPor c
         LEFT JOIN FETCH c.persona cp
         LEFT JOIN FETCH u.tipoCuota tc
         WHERE u.id = :id
         """)
  Optional<Usuario> findDetailById(@Param("id") Long id);

  // ============================================================
  // 🔎 NUEVO: Búsqueda por DNI, nombre o apellido
  // ============================================================
  @Query("""
        SELECT DISTINCT u
         FROM Usuario u
         LEFT JOIN FETCH u.persona p
        WHERE CAST(u.dni AS string) LIKE %:query%
           OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :query, '%'))
       """)
  List<Usuario> buscarPorDniONombreOApellido(@Param("query") String query);

  // Busca por DNI numérico
@Query("""
       SELECT DISTINCT u
       FROM Usuario u
       LEFT JOIN FETCH u.persona p
       WHERE CAST(u.dni AS string) LIKE CONCAT('%', :dni, '%')
       """)
List<Usuario> buscarPorDni(@Param("dni") String dni);

// Busca por nombre o apellido (solo texto)
@Query("""
       SELECT DISTINCT u
       FROM Usuario u
       LEFT JOIN FETCH u.persona p
       WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
          OR LOWER(p.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
       """)
List<Usuario> buscarPorNombreApellido(@Param("texto") String texto);

}
