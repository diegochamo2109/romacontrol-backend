package com.romacontrol.romacontrol_v1.repository.rol;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.Permiso;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    boolean existsByRutaIgnoreCase(String ruta);

    boolean existsByRutaIgnoreCaseAndIdNot(String ruta, Long id);

    Optional<Permiso> findByRutaIgnoreCase(String ruta);
}
