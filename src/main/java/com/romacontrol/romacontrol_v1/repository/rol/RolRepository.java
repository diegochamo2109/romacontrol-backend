package com.romacontrol.romacontrol_v1.repository.rol;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    Optional<Rol> findByNombreIgnoreCase(String nombre);

        List<Rol> findAllByOrderByNombreAsc();
}
