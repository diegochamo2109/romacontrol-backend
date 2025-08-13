package com.romacontrol.romacontrol_v1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.romacontrol.romacontrol_v1.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDni(String dni);
    
    
    @Query("select u.id from Usuario u where u.dni = :dni")
    
    Optional<Long> findIdByDni(@Param("dni") String dni);

    boolean existsByDni(String dni);
    List<Usuario> findByActivoTrue();
}
