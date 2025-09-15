package com.romacontrol.romacontrol_v1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.romacontrol.romacontrol_v1.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query("select u.id from Usuario u where u.dni = :dni")

    List<Usuario> findByActivoFalse();   // ✅ para filtrar inactivos también

  Optional<Usuario> findByDni(String dni);

  @Query("select u.id from Usuario u where u.dni = :dni")
  Optional<Long> findIdByDni(@Param("dni") String dni);

  boolean existsByDni(String dni);

  List<Usuario> findByActivoTrue();

  // ====== NUEVO: para listar con joins y evitar N+1 ======
  @Query("""
    select distinct u
    from Usuario u
    left join fetch u.persona p
    left join fetch u.roles r
    left join fetch u.creadoPor c
    left join fetch c.persona cp
  """)
  List<Usuario> findAllWithPersonaRolCreador();

  @Query("""
    select distinct u
    from Usuario u
    left join fetch u.persona p
    left join fetch u.roles r
    left join fetch u.creadoPor c
    left join fetch c.persona cp
    where u.activo = true
  """)
  List<Usuario> findActiveWithPersonaRolCreador();

  // ====== NUEVO: para detalle ======
  @Query("""
    select u
    from Usuario u
    left join fetch u.persona p
    left join fetch u.roles r
    left join fetch u.creadoPor c
    left join fetch c.persona cp
    left join fetch u.tipoCuota tc
    where u.id = :id
  """)
  Optional<Usuario> findDetailById(@Param("id") Long id);
}
